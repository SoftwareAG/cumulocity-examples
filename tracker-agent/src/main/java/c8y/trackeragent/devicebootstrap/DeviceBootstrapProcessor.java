/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.devicebootstrap;

import c8y.trackeragent.devicemapping.DeviceTenantMappingService;
import c8y.trackeragent.utils.TrackerPlatformProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;

@Component
public class DeviceBootstrapProcessor {

    protected static Logger logger = LoggerFactory.getLogger(DeviceBootstrapProcessor.class);

    private final DeviceCredentialsApi deviceCredentialsApi;
    private final DeviceTenantMappingService deviceTenantMappingService;

    @Autowired
    public DeviceBootstrapProcessor(TrackerPlatformProvider trackerPlatformProvider,
                                    DeviceTenantMappingService deviceTenantMappingService) {
        this.deviceCredentialsApi = trackerPlatformProvider.getBootstrapPlatform().getDeviceCredentialsApi();
        this.deviceTenantMappingService = deviceTenantMappingService;
    }
    
    public DeviceCredentials tryAccessDeviceCredentials(String imei) {    
        logger.info("Start bootstrapping: {}", imei);
        DeviceCredentialsRepresentation credentialsRepresentation = pollCredentials(imei);
        if (credentialsRepresentation == null) {
        	return null;
        } else {
        	return onNewDeviceCredentials(credentialsRepresentation);            
        }
    }

	private DeviceCredentials onNewDeviceCredentials(DeviceCredentialsRepresentation credentialsRep) { //save here addDeviceToTenant
		DeviceCredentials credentials = DeviceCredentials.forDevice(credentialsRep.getId(), credentialsRep.getTenantId());
		logger.info("Credentials for imei {} accessed: {}.", credentials.getImei(), credentials);
		deviceTenantMappingService.addDeviceToTenant(credentialsRep.getId(), credentialsRep.getTenantId());
		return credentials;
	}
    
    private DeviceCredentialsRepresentation pollCredentials(final String newDeviceRequestId) {
        try {
            return deviceCredentialsApi.pollCredentials(newDeviceRequestId);
        } catch (SDKException e) {
            if (e.getHttpStatus() == HttpStatus.NOT_FOUND.value()) {
                logger.debug("Credentials not yet available for device: " + newDeviceRequestId);
            } else {
                logger.error("Failed to retrieve credentials from cumulocity.", e);
            }
        }
        return null;
    }
}
