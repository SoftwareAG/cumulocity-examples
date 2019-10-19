package com.cumulocity.agent.snmp.platform.service;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.DeviceProtocolManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.GatewayDataRefreshedEvent;
import com.cumulocity.agent.snmp.platform.model.GatewayManagedObjectWrapper;
import com.cumulocity.agent.snmp.util.IpAddressUtil;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceCollectionRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.inventory.InventoryApi;
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
	private Map<String, DeviceManagedObjectWrapper> deviceProtocolMap = new HashMap<>();

	@Getter
	private Map<String, DeviceProtocolManagedObjectWrapper> protocolMap = new HashMap<>();

	public void updateGatewayObjects(ManagedObjectRepresentation gatewayManagedObject) {
		gatewayDevice = new GatewayManagedObjectWrapper(gatewayManagedObject);

		log.debug("Obtaining gateway device children and its corresponding device protocol mapping");
		refreshGatewayObjects();

		scheduleGatewayDataRefresh();
	}

	protected void refreshGatewayObjects() {
		Map<String, DeviceManagedObjectWrapper> newDeviceProtocolMap = new HashMap<>();
		Map<String, DeviceProtocolManagedObjectWrapper> newProtocolMap = new HashMap<>();

		GId id = gatewayDevice.getId();
		ManagedObjectRepresentation newGatewatDevice = inventoryApi.get(id);
		ManagedObjectReferenceCollectionRepresentation deviceCollections = newGatewatDevice.getChildDevices();
		GatewayManagedObjectWrapper newGatewatDeviceWrapper = new GatewayManagedObjectWrapper(newGatewatDevice);

		deviceCollections.forEach(childDeviceRep -> {
			try {
				ManagedObjectRepresentation childDeviceMo = inventoryApi.get(childDeviceRep.getManagedObject().getId());
				if (childDeviceMo.hasProperty(DeviceManagedObjectWrapper.C8Y_SNMP_DEVICE)) {
					updateDeviceProtocol(newDeviceProtocolMap, newProtocolMap, childDeviceMo);
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
			deviceProtocolMap = newDeviceProtocolMap;
			protocolMap = newProtocolMap;
		}
	}

	protected void scheduleGatewayDataRefresh() {
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

	private void updateDeviceProtocol(Map<String, DeviceManagedObjectWrapper> newDeviceProtocolMap,
			Map<String, DeviceProtocolManagedObjectWrapper> newProtocolMap, ManagedObjectRepresentation childDeviceMo) {

		DeviceManagedObjectWrapper childDeviceWrapper = new DeviceManagedObjectWrapper(childDeviceMo);
		String deviceIp = null;
		try {
			deviceIp = IpAddressUtil.sanitizeIpAddress(childDeviceWrapper.getProperties().getIpAddress());
		} catch(IllegalArgumentException iae) {
			log.error("Invalid IP Address <{}> specified in the SNMP device named {}.",
					childDeviceWrapper.getProperties().getIpAddress(),
					childDeviceMo.getName(), iae);

			deviceIp = childDeviceWrapper.getProperties().getIpAddress();
		}
		newDeviceProtocolMap.put(deviceIp, childDeviceWrapper);

		String protocolName = childDeviceWrapper.getDeviceProtocol();
		if (protocolName != null && !protocolName.isEmpty()) {
			updateProtocolMap(protocolName, newProtocolMap);
		} else {
			log.error("Missing device protocol configuration for the SNMP device {}", childDeviceMo.getName());
		}
	}

	private DeviceProtocolManagedObjectWrapper updateProtocolMap(String protocolName,
			Map<String, DeviceProtocolManagedObjectWrapper> newProtocolMap) {

		DeviceProtocolManagedObjectWrapper deviceProtocolWrapper = null;

		if (newProtocolMap.containsKey(protocolName)) {
			deviceProtocolWrapper = newProtocolMap.get(protocolName);
		} else {
			try {
				GId deviceProtocolId = new GId(protocolName);
				ManagedObjectRepresentation deviceProtocolMo = inventoryApi.get(deviceProtocolId);
				deviceProtocolWrapper = new DeviceProtocolManagedObjectWrapper(deviceProtocolMo);
				newProtocolMap.put(protocolName, deviceProtocolWrapper);
			} catch (SDKException sdk) {
				if (sdk.getHttpStatus() == HttpStatus.SC_NOT_FOUND) {
					log.error("{} device procotol managed object not found in the platform "
							+ "but configured in the device.", protocolName, sdk);
				} else {
					throw sdk;
				}
			}
		}

		return deviceProtocolWrapper;
	}
}
