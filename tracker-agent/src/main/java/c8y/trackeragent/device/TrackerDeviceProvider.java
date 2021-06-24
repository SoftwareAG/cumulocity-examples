/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.device;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.UserCredentials;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;

@Component
public class TrackerDeviceProvider {

	protected static Logger logger = LoggerFactory.getLogger(TrackerDeviceProvider.class);

	private final TrackerDeviceFactory trackerDeviceFactory;
	private final MicroserviceSubscriptionsService microserviceSubscriptionsService;

	@Autowired
	public TrackerDeviceProvider(TrackerDeviceFactory trackerDeviceFactory,
								 MicroserviceSubscriptionsService microserviceSubscriptionsService) {
		this.trackerDeviceFactory = trackerDeviceFactory;
		this.microserviceSubscriptionsService = microserviceSubscriptionsService;
	}

	public TrackerDevice getOrCreate(String tenant, String imei) {
		TrackerDevice device = ManagedObjectCache.instance().get(imei);
		if (device == null) {
			device = doCreate(tenant, imei);
		}
		return device;
	}

	private synchronized TrackerDevice doCreate(String tenant, String imei) {
		TrackerDevice device = ManagedObjectCache.instance().get(imei);
		if (device == null) {
			device = newTrackerDevice(tenant, imei);
			ManagedObjectCache.instance().put(device);
		}
		return device;
	}

	private TrackerDevice newTrackerDevice(String tenant, String imei) {
		return microserviceSubscriptionsService.callForTenant(tenant, () -> {
			return trackerDeviceFactory.newTrackerDevice(tenant, imei);
		});
	}
}
