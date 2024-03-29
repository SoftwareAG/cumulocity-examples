/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.coban.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.cumulocity.model.measurement.MeasurementValue;

import c8y.SpeedMeasurement;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.coban.CobanDeviceMessages;
import c8y.trackeragent.protocol.coban.device.CobanDevice;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.server.TestConnectionDetails;
import c8y.trackeragent.service.AlarmService;
import c8y.trackeragent.service.MeasurementService;

public abstract class CobanParserTestSupport {
    
    protected TrackerAgent trackerAgent;
    protected TrackerDevice deviceMock;
    protected CobanServerMessages serverMessages = new CobanServerMessages();
    protected CobanDeviceMessages deviceMessages = new CobanDeviceMessages();
    protected AlarmService alarmService = Mockito.mock(AlarmService.class);
    protected MeasurementService measurementService = Mockito.mock(MeasurementService.class);
    protected TestConnectionDetails connectionDetails = new TestConnectionDetails();

    @Before
    public void baseInit() {
        trackerAgent = mock(TrackerAgent.class);
        deviceMock = mock(TrackerDevice.class);
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(deviceMock);
    }
    
    protected void currentCobanDeviceIs(CobanDevice cobanDevice) {
        when(deviceMock.getCobanDevice()).thenReturn(cobanDevice);
    }
    
    protected void assertOut(String expected) throws UnsupportedEncodingException {
        assertThat(connectionDetails.getOut()).isEqualTo(expected);
    }
    
    public static class CreateSpeedMeasurementAnswer implements Answer<SpeedMeasurement> {

        @Override
        public SpeedMeasurement answer(InvocationOnMock invocation) throws Throwable {
            BigDecimal speed = (BigDecimal) invocation.getArguments()[0];
            SpeedMeasurement speedMeasurement = new SpeedMeasurement();
            MeasurementValue value = new MeasurementValue(speed, "km/h", null, null, null);
            speedMeasurement.setSpeed(value);
            return speedMeasurement;
        }
        
    };
}
