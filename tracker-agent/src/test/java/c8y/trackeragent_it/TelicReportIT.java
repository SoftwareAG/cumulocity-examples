/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent_it;

import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import org.assertj.core.api.Assertions;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import c8y.Position;
import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.protocol.telic.TelicDeviceMessages;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.message.TrackerMessage;
import org.springframework.beans.factory.annotation.Autowired;

public class TelicReportIT extends TrackerITSupport {
    
    private final TelicDeviceMessages deviceMessages = new TelicDeviceMessages();
    private String imei;

    @Autowired
    private MicroserviceSubscriptionsService microserviceSubscriptionsService;

    @Override
    protected TrackingProtocol getTrackerProtocol() {
        return TrackingProtocol.TELIC;
    }
    
    @Before
    public void init() {
        imei = Devices.randomImei();
    }

    @Test
    public void shouldBootstrapNewDeviceAndThenChangeItsLocation() throws Exception {
//        DeviceCredentialsApi deviceCredentialsApi = trackerPlatform.getDeviceCredentialsApi();
//        microserviceSubscriptionsService.runForEachTenant(() -> {
            try {
                bootstrapDevice(imei, deviceMessages.positionUpdate(imei, Positions.ZERO));
            } catch (Exception e) {
                throw new RuntimeException("Error setup devices", e);
            }
//        });
        
        TrackerMessage positionUpdate = deviceMessages.positionUpdate(imei, Positions.SAMPLE_4);
        writeInNewConnection(positionUpdate);
        
        Thread.sleep(1000);
        
        ManagedObjectRepresentation deviceMO = getDeviceMO(imei);
        Assertions.assertThat(deviceMO).isNotNull();
        Positions.assertEqual(deviceMO.get(Position.class), Positions.SAMPLE_4);
    }

}
