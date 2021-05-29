/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.device.TrackerDeviceProvider;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.exception.UnknownTenantException;
import c8y.trackeragent.utils.TrackerPlatformProvider;

@Component
public class TrackerAgent {
    
	private final DeviceCredentialsRepository credentialsRepository;
	private final TrackerPlatformProvider platformProvider;
	private final TrackerDeviceProvider trackerDeviceProvider;
	
    @Autowired
    public TrackerAgent(TrackerDeviceProvider trackerDeviceProvider, DeviceCredentialsRepository credentialsRepository,  TrackerPlatformProvider platformProvider) {
		this.trackerDeviceProvider = trackerDeviceProvider;
		this.credentialsRepository = credentialsRepository;
		this.platformProvider = platformProvider;
    }

    public TrackerDevice getOrCreateTrackerDevice(String imei) throws SDKException {
    	DeviceCredentials deviceCredentials = credentialsRepository.getDeviceCredentials(imei);
    	return trackerDeviceProvider.getOrCreate(deviceCredentials.getTenant(), imei);
    }
    
    public void finish(String deviceImei, OperationRepresentation operation) throws UnknownTenantException {
        operation.setStatus(OperationStatus.SUCCESSFUL.toString());
        getPlatform(deviceImei).getDeviceControlApi().update(operation);
    }

    public void fail(String deviceImei, OperationRepresentation operation, String text, SDKException ex) {
        operation.setStatus(OperationStatus.FAILED.toString());
        operation.setFailureReason(text + " " + ex.getMessage());
        getPlatform(deviceImei).getDeviceControlApi().update(operation);
    }
        
    private TrackerPlatform getPlatform(String imei) {
    	DeviceCredentials deviceCredentials = credentialsRepository.getDeviceCredentials(imei);
    	return platformProvider.getTenantPlatform(deviceCredentials.getTenant());
    }
}
