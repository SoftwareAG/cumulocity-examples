/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent_it.service;

import static com.cumulocity.rest.representation.operation.DeviceControlMediaType.NEW_DEVICE_REQUEST;
import static com.cumulocity.rest.representation.operation.DeviceControlMediaType.NEW_DEVICE_REQUEST_COLLECTION;

import java.util.List;

import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.rest.representation.devicebootstrap.NewDeviceRequestCollectionRepresentation;
import com.cumulocity.rest.representation.devicebootstrap.NewDeviceRequestRepresentation;
import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.ResponseParser;
import com.cumulocity.sdk.client.RestConnector;

import c8y.trackeragent_it.TestSettings;

public class NewDeviceRequestService {

    private static Logger logger = LoggerFactory.getLogger(NewDeviceRequestService.class);

    private final TestSettings testSettings;
    private final RestConnector restConnector;
    private final DeviceCredentialsApi deviceCredentialsApi;

    public NewDeviceRequestService(PlatformParameters platformParameters, TestSettings testSettings,
                                   DeviceCredentialsApi deviceCredentialsApi) {
        this.testSettings = testSettings;
        this.restConnector = new RestConnector(platformParameters, new ResponseParser());
        this.deviceCredentialsApi = deviceCredentialsApi;
    }

    public synchronized void create(String deviceId) {
        logger.info("Create newDeviceRequest for id: {}", deviceId);
        NewDeviceRequestRepresentation newDeviceRequest = get(deviceId);
        if (newDeviceRequest != null) {
            return;
        }
        NewDeviceRequestRepresentation representation = new NewDeviceRequestRepresentation();
        representation.setId(deviceId);
        restConnector.post(newDeviceRequestsUri(), NEW_DEVICE_REQUEST, representation);
    }

    public void accept(String deviceId) {
        logger.info("Accept newDeviceRequest for id: {}", deviceId);
        NewDeviceRequestRepresentation representation = new NewDeviceRequestRepresentation();
        representation.setStatus("ACCEPTED");
        NewDeviceRequestRepresentation request = restConnector.put(newDeviceRequestUri(deviceId), NEW_DEVICE_REQUEST, representation);
        logger.info("Get result after put: {}", request);
    }

    public NewDeviceRequestRepresentation get(String deviceId) {
        try {
            return restConnector.get(newDeviceRequestUri(deviceId), NEW_DEVICE_REQUEST, NewDeviceRequestRepresentation.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public List<NewDeviceRequestRepresentation> getAll() {
        try {
            NewDeviceRequestCollectionRepresentation colRep = restConnector.get(newDeviceRequestsUri(), NEW_DEVICE_REQUEST_COLLECTION,
                    NewDeviceRequestCollectionRepresentation.class);
            return colRep.getNewDeviceRequests();
        } catch (Exception ex) {
            return null;
        }
    }

    public void deleteAll() {
        List<NewDeviceRequestRepresentation> all = getAll();
        if (all == null) {
            return;
        }
        for (NewDeviceRequestRepresentation req : all) {
            delete(req.getId());
        }
    }

    public boolean exists(String deviceId) {
        return get(deviceId) != null;
    }

    public void delete(String deviceId) {
        logger.info("Delete newDeviceRequest for id: {}", deviceId);
        restConnector.delete(newDeviceRequestUri(deviceId));
    }

    public void deleteSilent(String deviceId) {
        if (exists(deviceId)) {
            delete(deviceId);
        }
    }

    private String newDeviceRequestsUri() {
        return testSettings.getC8yHost() + "/devicecontrol/newDeviceRequests";
    }

    private String newDeviceRequestUri(String deviceId) {
        return newDeviceRequestsUri() + "/" + deviceId;
    }

}
