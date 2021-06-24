/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.device;

import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;

import c8y.trackeragent.UpdateIntervalProvider;
import c8y.trackeragent.configuration.TrackerConfiguration;

@Component
public class TrackerDeviceFactory {
	
	private final TrackerConfiguration configuration;
    private final EventApi events;
    private final AlarmApi alarms;
    private final MeasurementApi measurements;
    private final DeviceControlApi deviceControl;
    private final IdentityApi registry;
    private final InventoryApi inventory;
    private final UpdateIntervalProvider updateIntervalProvider;
    private final MicroserviceSubscriptionsService microserviceSubscriptionsService;

    @Autowired
	public TrackerDeviceFactory(TrackerConfiguration configuration,
			EventApi events, AlarmApi alarms, MeasurementApi measurements, DeviceControlApi deviceControl,
			IdentityApi registry, InventoryApi inventory, UpdateIntervalProvider updateIntervalProvider,
								MicroserviceSubscriptionsService microserviceSubscriptionsService) {
		this.configuration = configuration;
		this.events = events;
		this.alarms = alarms;
		this.measurements = measurements;
		this.deviceControl = deviceControl;
		this.registry = registry;
		this.inventory = inventory;
		this.updateIntervalProvider = updateIntervalProvider;
		this.microserviceSubscriptionsService = microserviceSubscriptionsService;
	}

	/**
	 * TODO instead of device.init() - execute mo preparation here.
	 */
	public TrackerDevice newTrackerDevice(String tenant, String imei) {
		TrackerDevice device = new TrackerDevice(
				tenant, imei, configuration, events, alarms, measurements, deviceControl, registry, inventory, updateIntervalProvider
		);
		microserviceSubscriptionsService.runForTenant(tenant, () -> {
			device.init();
		});
		return device;
	}
}
