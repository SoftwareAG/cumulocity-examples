package com.cumulocity.agent.snmp.platform.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import com.cumulocity.agent.snmp.bootstrap.model.BootstrapReadyEvent;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.DeviceProtocolManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.GatewayDataRefreshedEvent;
import com.cumulocity.agent.snmp.platform.model.GatewayManagedObjectWrapper;
import com.cumulocity.agent.snmp.utils.Constants;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceCollectionRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
	public void handle(BootstrapReadyEvent event) {
		log.debug("Obtaining gateway device children and its corresponding device protocol mapping" + event);

		ManagedObjectRepresentation gatewayManagedObject = event.getGatewayDevice();
		gatewayDevice = new GatewayManagedObjectWrapper(gatewayManagedObject);

		updateGatewayObjects();
		scheduleGatewayDataRefresh();
	}

	private void updateGatewayObjects() {

		GId id = gatewayDevice.getId();
		ManagedObjectRepresentation newGatewatDevice = inventoryApi.get(id);
		GatewayManagedObjectWrapper newGatewatDeviceWrapper = new GatewayManagedObjectWrapper(newGatewatDevice);

		Map<GId, DeviceProtocolManagedObjectWrapper> newDeviceProtocolMap = new HashMap<>();
		ManagedObjectReferenceCollectionRepresentation deviceCollections = newGatewatDevice.getChildDevices();
		deviceCollections.forEach(new Consumer<ManagedObjectReferenceRepresentation>() {
			@Override
			public void accept(ManagedObjectReferenceRepresentation childDeviceRep) {
				ManagedObjectRepresentation childDeviceMo = inventoryApi.get(childDeviceRep.getManagedObject().getId());
				if (childDeviceMo.hasProperty(Constants.C8Y_SNMP_DEVICE)) {
					DeviceManagedObjectWrapper childDeviceWrapper = new DeviceManagedObjectWrapper(childDeviceMo);
					String type = childDeviceWrapper.getProperties().getType();
					if (type != null) {
						GId deviceProtocolID = getDeviceProtocolID(type);
						ManagedObjectRepresentation deviceProtocol = inventoryApi.get(deviceProtocolID);
						newDeviceProtocolMap.put(deviceProtocolID,
								new DeviceProtocolManagedObjectWrapper(deviceProtocol));
					} else {
						log.error("Missing device protocol configuration for the SNMP device {}",
								childDeviceMo.getName());
					}
				}
			}
		});

		gatewayDevice = newGatewatDeviceWrapper;
		currentDeviceProtocolMap = newDeviceProtocolMap;

		eventPublisher.publishEvent(new GatewayDataRefreshedEvent(gatewayDevice));
	}

	private void scheduleGatewayDataRefresh() {
		taskScheduler.scheduleWithFixedDelay(() -> {
			if (platformProvider.isPlatformAvailable()) {
				try {
					System.out.println("Refreshing gateway objects");
					updateGatewayObjects();
				} catch (Throwable t) {
					// Forcefully catching throwable, as we do not want to stop the scheduler on exception.
					log.error("Unable to refresh gateway managed objects", t);
				}
			} else {
				log.debug("Platform is unavailable. Waiting for {} seconds before retry.",
						gatewayProperties.getBootstrapFixedDelay() / 1000);
			}
		}, gatewayProperties.getBootstrapFixedDelay());
	}

	private GId getDeviceProtocolID(String type) {
		String[] data = type.trim().split("/");
		String deviceProtocol = data[data.length - 1];
		return new GId(deviceProtocol);
	}
}
