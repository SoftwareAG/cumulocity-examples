/*
 * Copyright (C) 2013 Cumulocity GmbH
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

package c8y.trackeragent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import c8y.MotionTracking;
import c8y.trackeragent.operations.OperationContext;

import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

public class GL200DeviceMotionStateTest {

    public static final String PASSWORD = "gl200";
    public static final String IMEI = "135790246811220";

    public static final String SETTRACKINGSTR = "AT+GTCFG=gl200,,,,,,,,,,47,,,,,,,,,,,0001$";

    public static final String ACKMOTIONSTR = "+ACK:GTCFG,02010B,135790246811220,,0001,20100310172830,11F0$";
    public static final String[] ACKMOTION = ACKMOTIONSTR.split(GL200Constants.FIELD_SEP);

    public static final String REPMOTIONSTR = "+RESP:GTSTT,02010B,135790246811220,,42,0,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,20100214093254,11F0$";
    public static final String[] REPMOTION = REPMOTIONSTR.split(GL200Constants.FIELD_SEP);
    
    public static final String GV500REPMOTSTR = "+RESP:GTSTT,1F0101,135790246811220,1G1JC5444R7252367,,16,0,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,20090214093254,11F0$";
    public static final String[] GV500REPMOTION = GV500REPMOTSTR.split(GL200Constants.FIELD_SEP);    
    
    private GL200DeviceMotionState gl200mot;
    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerDevice device = mock(TrackerDevice.class);
    private OperationContext operationCtx;
    private MotionTracking track = new MotionTracking();


    @Before
    public void setup() throws SDKException {
        OperationRepresentation operation = new OperationRepresentation();
        operation.set(track);
        operationCtx = new OperationContext(operation, IMEI);
        gl200mot = new GL200DeviceMotionState(trackerAgent, PASSWORD);
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);
    }

    @Test
    public void setMotionTracking() {
        track.setActive(false);
        String asciiOperation = gl200mot.translate(operationCtx);
        assertEquals(SETTRACKINGSTR, asciiOperation);

        track.setActive(true);
        asciiOperation = gl200mot.translate(operationCtx);
        assertEquals(SETTRACKINGSTR.replace("47", "303").replace("0001", "0002"), asciiOperation);
    }

    @Test
    public void ackMotionTracking() throws SDKException {
        track.setActive(false);
        gl200mot.translate(operationCtx);

        String imei = gl200mot.parse(ACKMOTION);
        ReportContext reportCtx = new ReportContext(ACKMOTION, imei, null);
        gl200mot.onParsed(reportCtx);

        assertEquals(IMEI, imei);
        verify(trackerAgent).getOrCreateTrackerDevice(IMEI);
        verify(device).setMotionTracking(false);
    }

    @Test
    public void motionReport() throws SDKException {
        String imei = gl200mot.parse(REPMOTION);
        ReportContext reportCtx = new ReportContext(REPMOTION, imei, null);
        gl200mot.onParsed(reportCtx);

        assertEquals(IMEI, imei);
        verify(trackerAgent).getOrCreateTrackerDevice(IMEI);
        verify(device).motionEvent(anyBoolean());
        verify(device).motionEvent(true);

        String[] repNoMotion = REPMOTIONSTR.split(GL200Constants.FIELD_SEP);
        repNoMotion[4] = "41";
        imei = gl200mot.parse(repNoMotion);
        reportCtx = new ReportContext(repNoMotion, imei, null);
        gl200mot.onParsed(reportCtx);
        verify(device, times(2)).motionEvent(anyBoolean());
        verify(device).motionEvent(false);
    }
    
    @Test
    public void gv500MotionReport() throws SDKException {
        String imei = gl200mot.parse(GV500REPMOTION);
        ReportContext reportCtx = new ReportContext(GV500REPMOTION, imei, null);
        gl200mot.onParsed(reportCtx);
        
        assertEquals(IMEI, imei);
        verify(device).motionEvent(true);       
    }    

}
