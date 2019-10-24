package com.cumulocity.agent.snmp.platform.service;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.*;
import com.cumulocity.agent.snmp.util.IpAddressUtil;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceCollectionRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.notification.Subscriber;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class GatewayDataProvider {

	@Autowired
	private InventoryApi inventoryApi;

	@Autowired
	private DeviceControlApi deviceControlApi;

	@Autowired
	private TaskScheduler taskScheduler;

	@Autowired
	private PlatformProvider platformProvider;

	@Autowired
	private GatewayProperties gatewayProperties;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Getter
	private GatewayManagedObjectWrapper gatewayDevice;

	@Getter
	private Map<String, DeviceManagedObjectWrapper> snmpDeviceMap = new HashMap<>();

	@Getter
	private Map<String, DeviceProtocolManagedObjectWrapper> protocolMap = new HashMap<>();

	private Subscriber<GId, OperationRepresentation> subscriberForOperationsOnGateway;

	public void updateGatewayObjects(ManagedObjectRepresentation gatewayManagedObject) {
		gatewayDevice = new GatewayManagedObjectWrapper(gatewayManagedObject);

		log.debug("Obtaining gateway device children and its corresponding device protocol mapping");
		refreshGatewayObjects();

		scheduleGatewayDataRefresh();
	}

	void refreshGatewayObjects() {
		Map<String, DeviceManagedObjectWrapper> newSnmpDeviceMap = new HashMap<>();
		Map<String, DeviceProtocolManagedObjectWrapper> newProtocolMap = new HashMap<>();

		GId id = gatewayDevice.getId();
		ManagedObjectRepresentation newGatewatDevice = inventoryApi.get(id);
		ManagedObjectReferenceCollectionRepresentation deviceCollections = newGatewatDevice.getChildDevices();
		GatewayManagedObjectWrapper newGatewatDeviceWrapper = new GatewayManagedObjectWrapper(newGatewatDevice);

		deviceCollections.forEach(childDeviceRep -> {
			try {
				ManagedObjectRepresentation childDeviceMo = inventoryApi.get(childDeviceRep.getManagedObject().getId());
				if (childDeviceMo.hasProperty(DeviceManagedObjectWrapper.C8Y_SNMP_DEVICE)) {
					updateDeviceProtocol(newSnmpDeviceMap, newProtocolMap, childDeviceMo);
				}
			} catch (SDKException sdkException) {
				if (sdkException.getHttpStatus() == HttpStatus.SC_NOT_FOUND) {
					log.error(
							"Unable to find child device object {} or its device protocol mapping object from platform",
							childDeviceRep.getManagedObject().getName());
				} else {
					throw sdkException;
				}
			}
		});

		synchronized (gatewayDevice) {
			gatewayDevice = newGatewatDeviceWrapper;
			snmpDeviceMap = newSnmpDeviceMap;
			protocolMap = newProtocolMap;
		}

		// Subscribe for Operations on Gateway Device
		subscribeForOperationsForGatewayDevice();
	}

	void scheduleGatewayDataRefresh() {
		taskScheduler.scheduleWithFixedDelay(() -> {
			if (platformProvider.isPlatformAvailable()) {
				try {
					log.debug("Refreshing gateway managed objects...");

					refreshGatewayObjects();

					eventPublisher.publishEvent(new GatewayDataRefreshedEvent(gatewayDevice));

					log.debug("Refreshing gateway managed objects completed.");
				} catch (Throwable t) {
					// Forcefully catching throwable, as we do not want to stop the scheduler on exception.
					log.error("Unable to refresh gateway managed objects", t);
				}
			} else {
				log.debug("Platform is unavailable. Waiting for {} minutes before retrying gateway data refresh.",
						gatewayProperties.getGatewayObjectRefreshIntervalInMinutes());
			}
		}, Duration.ofMinutes(gatewayProperties.getGatewayObjectRefreshIntervalInMinutes()));
	}

	private void updateDeviceProtocol(Map<String, DeviceManagedObjectWrapper> newSnmpDeviceMap,
			Map<String, DeviceProtocolManagedObjectWrapper> newProtocolMap, ManagedObjectRepresentation snmpDeviceMo) {

		DeviceManagedObjectWrapper childDeviceWrapper = new DeviceManagedObjectWrapper(snmpDeviceMo);
		String deviceIp;
		try {
			deviceIp = IpAddressUtil.sanitizeIpAddress(childDeviceWrapper.getProperties().getIpAddress());
		} catch(IllegalArgumentException iae) {
			log.error("Invalid IP Address <{}> specified in the SNMP device named {}.",
					childDeviceWrapper.getProperties().getIpAddress(),
					snmpDeviceMo.getName(), iae);

			deviceIp = childDeviceWrapper.getProperties().getIpAddress();
		}
		newSnmpDeviceMap.put(deviceIp, childDeviceWrapper);

		String protocolName = childDeviceWrapper.getDeviceProtocol();
		if (protocolName != null && !protocolName.isEmpty()) {
			updateProtocolMap(protocolName, newProtocolMap);
		} else {
			log.error("Missing device protocol configuration for the SNMP device {}", snmpDeviceMo.getName());
		}
	}

	private void updateProtocolMap(String protocolName, Map<String, DeviceProtocolManagedObjectWrapper> newProtocolMap) {

		DeviceProtocolManagedObjectWrapper deviceProtocolWrapper;

		if (!newProtocolMap.containsKey(protocolName)) {
			try {
				GId deviceProtocolId = new GId(protocolName);
				ManagedObjectRepresentation deviceProtocolMo = inventoryApi.get(deviceProtocolId);
				deviceProtocolWrapper = new DeviceProtocolManagedObjectWrapper(deviceProtocolMo);
				newProtocolMap.put(protocolName, deviceProtocolWrapper);
			} catch (SDKException sdk) {
				if (sdk.getHttpStatus() == HttpStatus.SC_NOT_FOUND) {
					log.error("{} device protocol managed object not found in the platform "
							+ "but configured in the device.", protocolName, sdk);
				} else {
					log.error(sdk.getMessage(), sdk);
					throw sdk;
				}
			}
		}
	}

	/**
	 * This subscription is renewed, every time the Gateway data is refreshed.
	 *
	 */
	private void subscribeForOperationsForGatewayDevice() {
		if(subscriberForOperationsOnGateway != null) {
			return;
		}

		try {
			subscriberForOperationsOnGateway = deviceControlApi.getNotificationsSubscriber();
			subscriberForOperationsOnGateway.subscribe(gatewayDevice.getId(), new SubscriptionListener<GId, OperationRepresentation>() {
				@Override
				public void onNotification(Subscription<GId> subscription, OperationRepresentation operation) {
					if(gatewayDevice.getId().equals(subscription.getObject())) {
						log.debug("Device '{}', with id '{}', received notification.",
								gatewayDevice.getName(), gatewayDevice.getId().getValue(), subscription.getObject().getValue());

						eventPublisher.publishEvent(new ReceivedOperationForGatewayEvent(gatewayDevice.getId(), gatewayDevice.getName(), operation));
					}
					else {
						log.debug("Device '{}', with id '{}', received a notification which is meant for device with id '{}'.",
								gatewayDevice.getName(), gatewayDevice.getId().getValue(), subscription.getObject().getValue());
					}
				}

				@Override
				public void onError(Subscription<GId> subscription, Throwable throwable) {
					log.error("Error occurred while listening to operations for the device with name '{}' and id '{}'.",
							gatewayDevice.getName(), gatewayDevice.getId().getValue(), throwable);
				}
			});

			log.info("Enabled the subscription for listening to operations for the device with name '{}' and id '{}'.",
					gatewayDevice.getName(), gatewayDevice.getId().getValue());
		} catch(Throwable t) {
			subscriberForOperationsOnGateway = null;

			// Ignore this exception and continue as the subscription will be retired when the Gateway data is refreshed next time.
			log.warn("Couldn't enable the subscription for listening to operations for the device with name '{}' and id '{}'." +
					" This subscription will be retired later.", gatewayDevice.getName(), gatewayDevice.getId().getValue());
			log.debug(t.getMessage(), t);
		}
	}
}
