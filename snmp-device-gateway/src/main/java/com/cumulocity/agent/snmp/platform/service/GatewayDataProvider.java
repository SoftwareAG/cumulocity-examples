package com.cumulocity.agent.snmp.platform.service;

import com.cumulocity.agent.snmp.bootstrap.model.BootstrapReadyEvent;
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
import org.springframework.context.event.EventListener;
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
	private Map<GId, DeviceProtocolManagedObjectWrapper> currentDeviceProtocolMap = new HashMap<>();

	@EventListener(BootstrapReadyEvent.class)
	protected void refreshGatewayObjects(BootstrapReadyEvent event) {
		log.debug("Obtaining gateway device children and its corresponding device protocol mapping" + event);

		ManagedObjectRepresentation gatewayManagedObject = event.getGatewayDevice();
		gatewayDevice = new GatewayManagedObjectWrapper(gatewayManagedObject);

		updateGatewayObjects();

		scheduleGatewayDataRefresh();
	}

	protected void updateGatewayObjects() {

		GId id = gatewayDevice.getId();
		ManagedObjectRepresentation newGatewatDevice = inventoryApi.get(id);
		GatewayManagedObjectWrapper newGatewatDeviceWrapper = new GatewayManagedObjectWrapper(newGatewatDevice);

		Map<GId, DeviceProtocolManagedObjectWrapper> newDeviceProtocolMap = new HashMap<>();
		ManagedObjectReferenceCollectionRepresentation deviceCollections = newGatewatDevice.getChildDevices();
		deviceCollections.forEach(childDeviceRep -> {
            try {
                ManagedObjectRepresentation childDeviceMo = inventoryApi.get(childDeviceRep.getManagedObject().getId());
                if (childDeviceMo.hasProperty(Constants.C8Y_SNMP_DEVICE)) {
                    DeviceManagedObjectWrapper childDeviceWrapper = new DeviceManagedObjectWrapper(childDeviceMo);
                    String type = childDeviceWrapper.getProperties().getType();
                    if (type != null) {
                        GId deviceProtocolID = getDeviceProtocolID(type);
                        ManagedObjectRepresentation deviceProtocol = inventoryApi.get(deviceProtocolID);
                        newDeviceProtocolMap.put(deviceProtocolID, new DeviceProtocolManagedObjectWrapper(deviceProtocol));
                    } else {
                        log.error("Missing device protocol configuration for the SNMP device {}", childDeviceMo.getName());
                    }
                }
            } catch (SDKException sdkException) {
                if (sdkException.getHttpStatus() == HttpStatus.SC_NOT_FOUND) {
                    log.error("Unable to find child device object {} or its device protocol mapping object from platform",
                            childDeviceRep.getManagedObject().getName());
                } else {
                    throw sdkException;
                }
            }
        });

		gatewayDevice = newGatewatDeviceWrapper;
		currentDeviceProtocolMap = newDeviceProtocolMap;

		eventPublisher.publishEvent(new GatewayDataRefreshedEvent(gatewayDevice));
	}

	protected void scheduleGatewayDataRefresh() {
		taskScheduler.scheduleWithFixedDelay(() -> {
			if (platformProvider.isPlatformAvailable()) {
				try {
					updateGatewayObjects();
				} catch (Throwable t) {
					// Forcefully catching throwable, as we do not want to stop the scheduler on exception.
					log.error("Unable to refresh gateway managed objects", t);
				}
			} else {
				log.debug("Platform is unavailable. Waiting for {} minutes before retrying gateway data refresh.", gatewayProperties.getGatewayObjectRefreshIntervalInMinutes());
			}
		}, Duration.ofMinutes(gatewayProperties.getGatewayObjectRefreshIntervalInMinutes()));
	}

	private GId getDeviceProtocolID(String type) {
		String[] data = type.trim().split("/");
		String deviceProtocol = data[data.length - 1];
		return new GId(deviceProtocol);
	}
}
