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

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import c8y.trackeragent.protocol.queclink.QueclinkConstants;
import c8y.trackeragent.protocol.queclink.device.QueclinkDevice;
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

    private Logger logger = LoggerFactory.getLogger(QueclinkDeviceMotionState.class);

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
            "AT+GTGBC=%s,,,,,,,,,,,,,,,%d,,,,,,,%04x$", // specific to gl500
            "AT+GTGBC=%s,,,,,,,,,,,,,,,%d,,,,,,,,%04x$" // specific to gl50x
    };

    /**
     * Events to set: Power on/off, external power on/off, battery low are
     * always on. Device motion is added depending on configuration from
     * platform.
     */
    public static final int MOTION_OFF = 1 + 2 + 4 + 8 + 32;
    public static final int MOTION_ON = 1 + 2 + 4 + 8 + 32 + 256;

    public static final String[] MOTION_ACK = {
            "+ACK:GTCFG",
            "+ACK:GTGBC"
    };
    
    public static final String REPORT_INTERVAL_NO_MOTION_ACK = "+ACK:GTNMD";

    /**
     * Set report interval on motion state
     * AT+GTFRI: Template parameters: password, send interval (in sec.), serial number 
     * AT+GTGBC: With this command motion report interval and sensor enable/disable options are given.
     *          Template parameters: password, send interval (in minutes), sensor enable/disable, serial number
     */
    public static final String[] REPORT_INTERVAL_ON_MOTION_TEMPLATE = {
            "AT+GTFRI=%s,1,,,,,,%d,%d,,,,,,,,,,,,%04x$", // specific to gl200, gl300
            "AT+GTGBC=%s,,,,,,,,,,,,,%s,,%d,,,,,,,%04x$", // specific to gl500
            "AT+GTGBC=%s,,,,,,,,,,,,,%s,,%d,,,,,,,,%04x$" // specific to gl50x 
    };


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
        if (MOTION_ACK[0].equals(reportType) || MOTION_ACK[1].equals(reportType)) {
            return onParsedAck(reportCtx.getReport(), reportCtx.getImei());
        } else if (REPORT_INTERVAL_NO_MOTION_ACK.equals(reportType)) {
           return onParsedTrackingAck(reportCtx, reportCtx.getImei());
        }  else if (MOTION_REPORT[0].equals(reportType) || MOTION_REPORT[1].equals(reportType)) {
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
        DateTime dateTime = getQueclinkDevice().getReportDateTime(report);
        device.motionEvent(motion, dateTime);
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

            if (ackMTrack.getInterval() > 0) {
                trackerAgent.getOrCreateTrackerDevice(imei).setMotionTracking(ackMTrack.isActive(), ackMTrack.getInterval());
            } else {
                trackerAgent.getOrCreateTrackerDevice(imei).setMotionTracking(ackMTrack.isActive());
            }
        } catch (SDKException x) {
            trackerAgent.fail(imei, ackOp, "Error setting motion tracking", x);
        }
        return true;
    }
    
    
    private boolean onParsedTrackingAck(ReportContext reportCtx, String imei) {
        short returnedCommandSerialNum = Short.parseShort(reportCtx.getEntry(4), 16);
        Tracking ackTracking;
        MotionTracking ackMTracking;

        synchronized(this) {
            if (returnedCommandSerialNum != corrId) {
                return false;
            }
            ackTracking = lastOperation.get(Tracking.class);
            ackMTracking = lastOperation.get(MotionTracking.class);
        }

        try {
            // store fragment to managed object
            trackerAgent.getOrCreateTrackerDevice(imei).setTracking(ackTracking.getInterval());
            
        } catch (SDKException sdkException) {
            trackerAgent.fail(imei, lastOperation, "Error setting tracking to managed object", sdkException);
        }

        return true;
    }

    @Override
    public String translate(OperationContext operationCtx) {
        OperationRepresentation operation = operationCtx.getOperation();
        MotionTracking mTrack = operation.get(MotionTracking.class);
        Tracking tracking = operation.get(Tracking.class);
        
        if (mTrack == null && tracking == null) {
            return null;
        }

        synchronized (this) {
            corrId++;
            lastOperation = operation;
        }

        String deviceCommand = new String();
        if (mTrack != null) {
            deviceCommand += setMotionTrackingOptionsOnDevice(operation.getDeviceId(), mTrack);
        }
        
        if (tracking != null) {
            deviceCommand += setTrackingOptionsOnDevice(operation.getDeviceId(), tracking);
        }
        
        return (deviceCommand.isEmpty())? null : deviceCommand;
    }

    private String setMotionTrackingOptionsOnDevice(GId deviceId, MotionTracking mTrack) {
        
        ManagedObjectRepresentation deviceMo = getQueclinkDevice().getManagedObjectFromGId(deviceId);
        String password = (String) deviceMo.get("password");

        String deviceCommand = new String(); 

        // if active field is false.
        // update the gtfri report interval (c8y_MotionTracking) as gtnmd (c8y_Tracking) 

        int intervalInSeconds = 0;
        if (mTrack.isActive()) {
            if (mTrack.getInterval() > 0) {
                intervalInSeconds = mTrack.getInterval();
            } 
        } else {
            //set the value from c8y_Tracking
            Tracking track = deviceMo.get(Tracking.class);
            if (track.getInterval() > 0) {
                intervalInSeconds = track.getInterval();
            }  
        }


        if (intervalInSeconds > 0) {
            // With this command motion report interval and sensor enable/disable options are given
            deviceCommand += getQueclinkDevice().getDeviceByType(deviceMo.getType()).configureMotionTrackingCommand(password, mTrack.isActive(), intervalInSeconds, corrId);
        } else {
            
            deviceCommand += getQueclinkDevice().getDeviceByType(deviceMo.getType()).configureMotionTrackingCommand(password, mTrack.isActive(), corrId);
        }

        //update operation
        mTrack.setInterval(intervalInSeconds);
        lastOperation.set(mTrack);

        // add restart command
        //deviceCommand += String.format("AT+GTRTO=%s,3,,,,,,0001$", password);
        return deviceCommand;
    }
    
    private String setTrackingOptionsOnDevice(GId deviceId, Tracking tracking) {
        
        String deviceCommand = new String();
        
        ManagedObjectRepresentation deviceMo = getQueclinkDevice().getManagedObjectFromGId(deviceId);
        String password = (String) deviceMo.get("password");
        
        int intervalInSeconds = tracking.getInterval();
       
        if (intervalInSeconds > 0) {
 
            MotionTracking mTracking = deviceMo.get(MotionTracking.class);
            if (!mTracking.isActive()) {
                deviceCommand += getQueclinkDevice().getDeviceByType(deviceMo.getType()).configureMotionTrackingCommand(password, mTracking.isActive(), intervalInSeconds, corrId);
                
                //update operation
                mTracking.setInterval(intervalInSeconds);
                lastOperation.set(mTracking);
            }
            
            deviceCommand += getQueclinkDevice().getDeviceByType(deviceMo.getType()).configureTrackingCommand(password, intervalInSeconds, corrId);
        }
     
        return deviceCommand;
    }

}
