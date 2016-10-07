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

package c8y.trackeragent.protocol.queclink.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.MotionTracking;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.QueclinkConstants;
import c8y.trackeragent.tracker.Translator;

/**
 * <p>
 * Device Motion State indication protocol of GL200 tracker. Samples below show:
 * getting device motion state indication report. This event is triggered when a
 * certain event occur.
 * </p>
 * 
 * <pre>
 * +RESP:GTSTT,02010B,135790246811220,,41,0,4.3,92,70.0,121.354335,31.222073,2009021,4013254,0460,0000,18d8,6141,00,20100214093254,11F0$
 * </pre>
 */
@Component
public class QueclinkDeviceMotionState extends QueclinkParser implements Translator {

    /**
     * Type of report: Device Motion State Indication.
     */
    public static final String[] MOTION_REPORT = {
            "+RESP:GTSTT",
            "+RESP:GTNMR"
    };

    public static final String MOTION_DETECTED_IGNITION_OFF = "12";
    public static final String MOTION_DETECTED_IGNITION_ON = "22";
    public static final String MOTION_DETECTED = "42";
    public static final String BEING_TOWED = "16";
    
    /**
     * Change the event mask to include motion tracking and enable/disable sensor.
     */
    public static final String[] MOTION_TEMPLATE = {
            "AT+GTCFG=%s,,,,,,,,,,%d,%d,,,,,,,,,,%04x$", // specific to gl200, gl300, gv500
            "AT+GTGBC=%s,,,,,,,,,,,,,%s,,%d,,,,,,,,%04x$" // specific to gl50x
    };

    /**
     * Events to set: Power on/off, external power on/off, battery low are
     * always on. Device motion is added depending on configuration from
     * platform.
     */
    public static final int MOTION_OFF = 1 + 2 + 4 + 8 + 32;
    public static final int MOTION_ON = 1 + 2 + 4 + 8 + 32 + 256;

    public static final String MOTION_ACK = "+ACK:GTCFG";

    /**
     * Set report interval on motion state
     * Template parameters: password, send interval, serial number
     */
    public static final String REPORT_INTERVAL_ON_MOTION_TEMPLATE = "AT+GTFRI=%s,1,,,,,,,%d,,,,,,,,,,,,%04x$";
    
    public static final String REPORT_INTERVAL_ON_MOTION_ACK = "+ACK:GTFRI";
    
    private final TrackerAgent trackerAgent;
    private short corrId = 1;
    private OperationRepresentation lastOperation;


    @Autowired
    public QueclinkDeviceMotionState(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        String reportType = reportCtx.getReport()[0];
        if (MOTION_ACK.equals(reportType)) {
            return onParsedAck(reportCtx.getReport(), reportCtx.getImei());
        } else if (MOTION_REPORT[0].equals(reportType) || MOTION_REPORT[1].equals(reportType)) {
            return onParsedMotion(reportCtx.getReport(), reportCtx.getImei());
        } else {
            return false;
        }
    }

    private boolean onParsedMotion(String[] report, String imei) throws SDKException {
        
        
        
        String deviceType = report[1].substring(0, 2);
        boolean motion; 
        if (report[0].equals(MOTION_REPORT[1])) {
            String motionState = report[5];
            motion = motionState.equals("1");
            
        }  else {
            String motionState = report[4];
            
            if (QueclinkConstants.GV500_ID.equals(deviceType)) {
                motionState = report[5];
                }
            
            motion = MOTION_DETECTED.equals(motionState)
                    || MOTION_DETECTED_IGNITION_OFF.equals(motionState)
                    || MOTION_DETECTED_IGNITION_ON.equals(motionState)
                    || BEING_TOWED.equals(motionState);
            
        }
        
        
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(imei);
        device.motionEvent(motion);
        return true;
    }

    private boolean onParsedAck(String[] report, String imei) throws SDKException {
        short returnedCorr = Short.parseShort(report[4], 16);
        OperationRepresentation ackOp;
        MotionTracking ackMTrack;
        
        synchronized (this) {
            if (returnedCorr != corrId) {
                return false;
            }
            ackOp = lastOperation;
            ackMTrack = ackOp.get(MotionTracking.class);
        }

        try {
            
            if (ackMTrack.getInterval() >= 0) {
                trackerAgent.getOrCreateTrackerDevice(imei).setMotionTracking(ackMTrack.isActive(), ackMTrack.getInterval());
            } else {
                trackerAgent.getOrCreateTrackerDevice(imei).setMotionTracking(ackMTrack.isActive());
            }
            
            trackerAgent.finish(imei, ackOp);
        } catch (SDKException x) {
            trackerAgent.fail(imei, ackOp, "Error setting motion tracking", x);
        }
        return true;
    }

    @Override
    public String translate(OperationContext operationCtx) {
        OperationRepresentation operation = operationCtx.getOperation();
        MotionTracking mTrack = operation.get(MotionTracking.class);

        if (mTrack == null) {
            return null;
        }

        synchronized (this) {
            corrId++;
            lastOperation = operation;
        }
        
        ManagedObjectRepresentation deviceMo = queclinkDevice.getManagedObjectFromGId(operation.getDeviceId());
        String password = (String) deviceMo.get("password");
        
        String device_command = new String(); 
        
        if (queclinkDevice.convertDeviceTypeToQueclinkType(QueclinkConstants.GL500_ID).equals(deviceMo.getType()) ||
                queclinkDevice.convertDeviceTypeToQueclinkType(QueclinkConstants.GL505_ID).equals(deviceMo.getType())) {
            
            String intervalVal = "";
            
            if (mTrack.getInterval() >= 0) {
                int intervalMin = mTrack.getInterval() / 60; 
                intervalVal = Integer.toString(intervalMin);
            }
            
            device_command = String.format(MOTION_TEMPLATE[1],
                    password,
                    intervalVal,
                    mTrack.isActive() ? 1 : 0, corrId);

        } else {
            if (mTrack.getInterval() >= 0) {
                int interval = mTrack.getInterval();
                device_command += String.format(REPORT_INTERVAL_ON_MOTION_TEMPLATE, password, interval, corrId);
            }
            
            device_command += String.format(MOTION_TEMPLATE[0], password, mTrack.isActive() ? MOTION_ON : MOTION_OFF, mTrack.isActive() ? 1 : 0, corrId);

        }
        
        
        // add restart command
        device_command += String.format("AT+GTRTO=%s,3,,,,,,0001$", password);
        
        return device_command;
    }
}
