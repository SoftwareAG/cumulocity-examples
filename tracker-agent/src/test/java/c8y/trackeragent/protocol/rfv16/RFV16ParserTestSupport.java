/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.rfv16;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;

import c8y.RFV16Config;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.UpdateIntervalProvider;
import c8y.trackeragent.protocol.rfv16.message.RFV16DeviceMessages;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.protocol.rfv16.parser.RFV16AlarmType;
import c8y.trackeragent.server.TestConnectionDetails;
import c8y.trackeragent.service.AlarmService;
import c8y.trackeragent.service.MeasurementService;

public class RFV16ParserTestSupport {
    
    protected static final String IMEI = "1234567890";
    protected static final String SOME_DATE_TIME_STR = "010000";
    protected static final DateTime SOME_DATE_TIME = RFV16ServerMessages.HHMMSS.parseDateTime(SOME_DATE_TIME_STR);
    
    protected TrackerAgent trackerAgent  = mock(TrackerAgent.class);
    protected TrackerDevice deviceMock = mock(TrackerDevice.class); 
    protected RFV16ServerMessages serverMessages = new RFV16ServerMessages();
    protected RFV16DeviceMessages deviceMessages = new RFV16DeviceMessages();
    protected MeasurementService measurementService = mock(MeasurementService.class);
    protected AlarmService alarmService = mock(AlarmService.class);
    protected TestConnectionDetails connectionDetails = new TestConnectionDetails(IMEI);
    private UpdateIntervalProvider updateIntervalProvider = mock(UpdateIntervalProvider.class);
    
    
    @Before
    public void baseInit() {
        DateTimeUtils.setCurrentMillisFixed(SOME_DATE_TIME.getMillis());
        when(trackerAgent.getOrCreateTrackerDevice(Mockito.anyString())).thenReturn(deviceMock);
        when(deviceMock.getGId()).thenReturn(GId.asGId("1001"));
        when(deviceMock.aLocationUpdateEvent()).thenReturn(new EventRepresentation());
        when(deviceMock.getUpdateIntervalProvider()).thenReturn(updateIntervalProvider);
        when(updateIntervalProvider.findUpdateInterval()).thenReturn(null);
    }
    
    protected void currentDeviceConfigIs(RFV16Config rFV16Config) {
        when(deviceMock.getRFV16Config()).thenReturn(rFV16Config);
    }
    
    protected void assertOut(String expected) throws UnsupportedEncodingException {
        assertThat(connectionDetails.getOut()).isEqualTo(expected);
    }
    
    protected void assertNothingOut() throws UnsupportedEncodingException {
        assertThat(connectionDetails.getOut()).isNull();
    }

    public static class CreateAlarmAnswer implements Answer<AlarmRepresentation> {

        @Override
        public AlarmRepresentation answer(InvocationOnMock invocation) throws Throwable {
            RFV16AlarmType alarmType = (RFV16AlarmType) invocation.getArguments()[1];
            AlarmRepresentation alarmRepresentation = new AlarmRepresentation();
            alarmRepresentation.setText(alarmType.name());
            return alarmRepresentation;
        }
        
    };

    
}
