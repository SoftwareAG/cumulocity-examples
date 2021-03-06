/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.service;

import c8y.trackeragent.devicebootstrap.MicroserviceSubscriptionsServiceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.device.TrackerDeviceProvider;
import c8y.trackeragent.protocol.TrackingProtocol;

@Component
public class TrackerDeviceContextService {

	protected static Logger logger = LoggerFactory.getLogger(TrackerDeviceContextService.class);

	private final MicroserviceSubscriptionsServiceWrapper microserviceSubscriptionsServiceWrapper;
    private final TrackerDeviceProvider trackerDeviceFactory;
    
    @Autowired
	public TrackerDeviceContextService(
			MicroserviceSubscriptionsServiceWrapper microserviceSubscriptionsServiceWrapper,
			TrackerDeviceProvider trackerDeviceFactory) {
		this.microserviceSubscriptionsServiceWrapper = microserviceSubscriptionsServiceWrapper;
		this.trackerDeviceFactory = trackerDeviceFactory;
	}
    
	public void executeWithContext(String tenant, Runnable runnable) {
		executeForTenant(tenant, runnable);
	}

    public void executeWithContext(String tenant, String imei, TrackingProtocol trackingProtocol, Runnable runnable) {
        TrackerDevice device = trackerDeviceFactory.getOrCreate(tenant, imei);
        if (trackingProtocol != null) {
            device.setTrackingProtocolInfo(trackingProtocol);
        }
		executeForTenant(tenant, runnable);
    }
	
	private void executeForTenant(String tenant, Runnable r) {
		microserviceSubscriptionsServiceWrapper.runForTenant(tenant, r);
	}
}
