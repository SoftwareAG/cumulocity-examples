/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package c8y.trackeragent.protocol.queclink;

import static c8y.trackeragent.protocol.TrackingProtocol.QUECLINK;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.Geofence;
import c8y.Position;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.device.QueclinkDevice;
import c8y.trackeragent.protocol.queclink.parser.QueclinkGeofence;
import c8y.trackeragent.server.TestConnectionDetails;
import c8y.trackeragent.service.MeasurementService;
import c8y.trackeragent.utils.QueclinkReports;

public class GL200GeofenceTest {

    public static final String IMEI = "135790246811220";

    public static final String SETFENCESTR = "AT+GTGEO=gl200,0,3,101.412248,21.187891,1000,30,,,,,,,,,0001$";
    public static final String[] SETFENCE = SETFENCESTR.split(QUECLINK.getFieldSeparator());
    public static final String[] ACKFENCE = "+ACK:GTGEO,02010B,135790246811220,,0,0001,20100310172830,11F0$".split(QUECLINK.getFieldSeparator());
    public static final String[] REPFENCE = "+RESP:GTGEO,02010B,135790246811220,,0,0,1,1,4.3,92,70.0,121.354335,31.222073,2009 0214013254,0460,0000,18d8,6141,00,,20090214093254,11F0$"
            .split(QUECLINK.getFieldSeparator());

    private QueclinkGeofence gl200gf;
    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerDevice device = mock(TrackerDevice.class);
    private MeasurementService measurementService = mock(MeasurementService.class);
    private QueclinkDevice queclinkDevice = mock(QueclinkDevice.class);
    
    private OperationContext operationCtx;
    private Geofence fence;
    private TestConnectionDetails connectionDetails = new TestConnectionDetails();
    private ManagedObjectRepresentation managedObject;
    
    @Before
    public void setup() throws SDKException {
        OperationRepresentation operation = new OperationRepresentation();
        fence = new Geofence();
        fence.setLng(new BigDecimal("101.412248"));
        fence.setLat(new BigDecimal("21.187891"));
        fence.setRadius(new BigDecimal("1000"));
        fence.setActive(true);
        operation.set(fence);

        operationCtx = new OperationContext(connectionDetails, operation);
        
        gl200gf = new QueclinkGeofence(trackerAgent, measurementService);

        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);
    }
    
    public void setupTranslate() {
        gl200gf = spy(new QueclinkGeofence(trackerAgent, measurementService));
        managedObject = new ManagedObjectRepresentation();
        managedObject.setType("queclink_gl200");
        when(gl200gf.getQueclinkDevice()).thenReturn(queclinkDevice);
        when(queclinkDevice.getManagedObjectFromGId(nullable(GId.class))).thenReturn(managedObject);
    }

    @Test
    public void setGeofence() {
        setupTranslate();
        String asciiOperation = gl200gf.translate(operationCtx);
        assertEquals(SETFENCESTR, asciiOperation);
    }

    @Test
    public void acknowledgeGeofence() throws SDKException {
        setupTranslate();
        gl200gf.translate(operationCtx);
        connectionDetails.setImei(gl200gf.parse(ACKFENCE));
        gl200gf.onParsed(new ReportContext(connectionDetails, ACKFENCE));

        assertEquals(IMEI, gl200gf.parse(ACKFENCE));
        verify(trackerAgent).getOrCreateTrackerDevice(IMEI);
        verify(device).setGeofence(fence);
    }

    @Test
    public void acknowledgeGeofenceWrongReply() throws SDKException {
        setupTranslate();
        gl200gf.translate(operationCtx);

        String[] wrongCorrelation = ACKFENCE;
        wrongCorrelation[5] = "0002";
        gl200gf.parse(wrongCorrelation);

        verifyNoInteractions(trackerAgent);
    }

    @Test
    public void reportGeofence() throws SDKException {
        String imei = gl200gf.parse(REPFENCE);
        connectionDetails.setImei(imei);
        gl200gf.onParsed(new ReportContext(connectionDetails, REPFENCE));

        assertEquals(IMEI, imei);
        verify(trackerAgent, times(2)).getOrCreateTrackerDevice(IMEI);

        Position position = new Position();
        position.setLat(new BigDecimal("31.222073"));
        position.setLng(new BigDecimal("121.354335"));
        position.setAlt(new BigDecimal("70.0"));
        verify(device).setPosition(position, QueclinkReports.convertEntryToDateTime("20090214093254"));

        verify(device).geofenceAlarm(true);
    }

    // TODO Verify the formatting of commands sent to the device (limits and
    // lengths)

}
