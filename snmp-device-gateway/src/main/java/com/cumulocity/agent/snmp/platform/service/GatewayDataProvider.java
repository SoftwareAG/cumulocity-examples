package com.cumulocity.agent.snmp.platform.service;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.DeviceProtocolManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.GatewayDataRefreshedEvent;
import com.cumulocity.agent.snmp.platform.model.GatewayManagedObjectWrapper;
import com.cumulocity.agent.snmp.utils.Constants;
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
				if (childDeviceMo.hasProperty(Constants.C8Y_SNMP_DEVICE)) {
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
					// Forcefully catching throwable, as we do not want to stop the scheduler on
					// exception.
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
		String deviceProtocolName = childDeviceWrapper.getDeviceProtocol();
		if (deviceProtocolName != null) {
			DeviceProtocolManagedObjectWrapper deviceProtocolWrapper = fetchDeviceProtocol(deviceProtocolName,
					newProtocolMap);
			newDeviceProtocolMap.put(childDeviceWrapper.getProperties().getIpAddress().toLowerCase(),
					childDeviceWrapper);
			newProtocolMap.put(deviceProtocolName, deviceProtocolWrapper);
		} else {
			log.error("Missing device protocol configuration for the SNMP device {}", childDeviceMo.getName());
		}
	}

	private DeviceProtocolManagedObjectWrapper fetchDeviceProtocol(String deviceProtocol,
			Map<String, DeviceProtocolManagedObjectWrapper> newProtocolMap) {

		DeviceProtocolManagedObjectWrapper deviceProtocolWrapper = null;

		if (newProtocolMap.containsKey(deviceProtocol)) {
			deviceProtocolWrapper = newProtocolMap.get(deviceProtocol);
		} else {
			try {
				GId deviceProtocolId = new GId(deviceProtocol);
				ManagedObjectRepresentation deviceProtocolMo = inventoryApi.get(deviceProtocolId);
				deviceProtocolWrapper = new DeviceProtocolManagedObjectWrapper(deviceProtocolMo);
				newProtocolMap.put(deviceProtocol, deviceProtocolWrapper);
			} catch (SDKException sdk) {
				if (sdk.getHttpStatus() == HttpStatus.SC_NOT_FOUND) {
					log.error(
							"{} device procotol managed object not found in the platform but configured in the device.",
							deviceProtocol, sdk);
				} else {
					throw sdk;
				}
			}
		}

		return deviceProtocolWrapper;
	}
}
