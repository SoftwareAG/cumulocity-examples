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
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.MotionTracking;
import c8y.Tracking;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.device.BaseQueclinkDevice;
import c8y.trackeragent.protocol.queclink.device.GL200;
import c8y.trackeragent.protocol.queclink.device.QueclinkDevice;
import c8y.trackeragent.protocol.queclink.parser.QueclinkDeviceMotionState;
import c8y.trackeragent.protocol.queclink.parser.QueclinkIgnition;
import c8y.trackeragent.server.TestConnectionDetails;

public class GL200DeviceMotionStateTest {

    public static final String IMEI = "135790246811220";

    public static final String SETNOTRACKINGSTR = "AT+GTCFG=gl200,,,,,,,,,,47,0,,,,,,,,,,0002$";
    public static final String SETTRACKINGSTR = "AT+GTCFG=gl200,,,,,,,,,,303,1,,,,,,,,,,0003$";

    public static final String ACKMOTIONSTR = "+ACK:GTCFG,02010B,135790246811220,,0002,20100310172830,11F0$";
    public static final String[] ACKMOTION = ACKMOTIONSTR.split(QUECLINK.getFieldSeparator());

    public static final String REPMOTIONSTR = "+RESP:GTSTT,02010B,135790246811220,,42,0,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,20100214093254,11F0$";
    public static final String[] REPMOTION = REPMOTIONSTR.split(QUECLINK.getFieldSeparator());
    
    public static final String GV500REPMOTSTR = "+RESP:GTSTT,1F0101,135790246811220,1G1JC5444R7252367,,16,0,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,20090214093254,11F0$";
    public static final String[] GV500REPMOTION = GV500REPMOTSTR.split(QUECLINK.getFieldSeparator());    
    
    private QueclinkDeviceMotionState gl200mot;
    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerDevice device = mock(TrackerDevice.class);
    private QueclinkIgnition ignition = mock(QueclinkIgnition.class);
    private OperationContext operationCtx;
    private MotionTracking mtrack = new MotionTracking();
    private TestConnectionDetails connectionDetails = new TestConnectionDetails(IMEI);
    private QueclinkDevice queclinkDevice;
    private ManagedObjectRepresentation managedObject;
    private Tracking track = new Tracking();
    private BaseQueclinkDevice gl200 = new GL200();

    @Before
    public void setup() throws SDKException {
        OperationRepresentation operation = new OperationRepresentation();
        operation.set(mtrack);
        operation.setDeviceId(new GId("0"));
        operationCtx = new OperationContext(connectionDetails, operation);
        gl200mot = new QueclinkDeviceMotionState(trackerAgent, ignition);
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);
        
    }
    
    public void translate_setup() {
        gl200mot = spy(new QueclinkDeviceMotionState(trackerAgent, ignition));
        queclinkDevice = mock(QueclinkDevice.class);
        
        when(gl200mot.getQueclinkDevice()).thenReturn(queclinkDevice);
        when(queclinkDevice.getDeviceByType(anyString())).thenReturn(gl200);
    
        // prepare managed object
        managedObject = new ManagedObjectRepresentation();
        managedObject.setType("queclink_" + "gl200");
        when(queclinkDevice.getManagedObjectFromGId(any(GId.class))).thenReturn(managedObject);
        managedObject.set(track);

    }

    @Test
    public void setMotionTracking() {
        
        translate_setup();
        
        mtrack.setActive(false);
        String asciiOperation = gl200mot.translate(operationCtx);
        assertEquals(SETNOTRACKINGSTR, asciiOperation);

        mtrack.setActive(true);
        asciiOperation = gl200mot.translate(operationCtx);
        assertEquals(SETTRACKINGSTR, asciiOperation);
    }

    @Test
    public void ackMotionTracking() throws SDKException {
        
        translate_setup();
        
        mtrack.setActive(false);
        gl200mot.translate(operationCtx);

        String imei = gl200mot.parse(ACKMOTION);
        ReportContext reportCtx = new ReportContext(connectionDetails, ACKMOTION);
        gl200mot.onParsed(reportCtx);

        assertEquals(IMEI, imei);
        verify(trackerAgent).getOrCreateTrackerDevice(IMEI);
        verify(device).setMotionTracking(false);
    }

    @Test
    public void motionReport() throws SDKException {
        String imei = gl200mot.parse(REPMOTION);
        ReportContext reportCtx = new ReportContext(connectionDetails, REPMOTION);
        gl200mot.onParsed(reportCtx);

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        DateTime dateTime = formatter.parseDateTime("20100214093254");
        
        assertEquals(IMEI, imei);
        verify(trackerAgent).getOrCreateTrackerDevice(IMEI);
        verify(device).motionEvent(anyBoolean(), any(DateTime.class));
        verify(device).motionEvent(true, dateTime);

        String[] repNoMotion = REPMOTIONSTR.split(QUECLINK.getFieldSeparator());
        repNoMotion[4] = "41";
        imei = gl200mot.parse(repNoMotion);
        reportCtx = new ReportContext(connectionDetails, repNoMotion);
        gl200mot.onParsed(reportCtx);
        verify(device, times(2)).motionEvent(anyBoolean(), any(DateTime.class));
        
        verify(device).motionEvent(false, dateTime);
    }
    
    @Test
    public void gv500MotionReport() throws SDKException {
        String imei = gl200mot.parse(GV500REPMOTION);
        ReportContext reportCtx = new ReportContext(connectionDetails, GV500REPMOTION);
        gl200mot.onParsed(reportCtx);
        
        assertEquals(IMEI, imei);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        DateTime dateTime = formatter.parseDateTime("20090214093254");
        verify(device).motionEvent(true, dateTime);       
    }    

}
