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

    private final TrackerAgent trackerAgent;
    private short corrId = 1;
    private OperationRepresentation lastOperation;
    
    private final QueclinkIgnition queclinkIgnition;


    @Autowired
    public QueclinkDeviceMotionState(TrackerAgent trackerAgent, QueclinkIgnition queclinkIgnition) {
        this.trackerAgent = trackerAgent;
        this.queclinkIgnition = queclinkIgnition;
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        String reportType = reportCtx.getReport()[0];
        if (MOTION_ACK[0].equals(reportType) || MOTION_ACK[1].equals(reportType)) {
            return onParsedMTrackingAck(reportCtx.getReport(), reportCtx.getImei());
        } else if (REPORT_INTERVAL_NO_MOTION_ACK.equals(reportType)) {
           return onParsedTrackingAck(reportCtx, reportCtx.getImei());
        }  else if (MOTION_REPORT[0].equals(reportType) || MOTION_REPORT[1].equals(reportType)) {
            return onParsedMotion(reportCtx, reportCtx.getImei());
        } else {
            return false;
        }
    }

    private boolean onParsedMotion(ReportContext reportCtx, String imei) throws SDKException {
        
        parseMotionStateAndCreateEvent(reportCtx.getReport(), imei);
        
        if (reportCtx.getEntry(0).equals(MOTION_REPORT[0])) {
            parseIgnitionAndCreateEvent(reportCtx, imei);
            parseTowAndCreateEvent(reportCtx, imei);
        }
        
        return true;
    }
    
    private void parseTowAndCreateEvent(ReportContext reportCtx, String imei) {
        if (reportCtx.getEntry(4).equals(BEING_TOWED)) {
            TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(imei);
            DateTime reportTime = queclinkReport.getReportDateTime(reportCtx);
            device.towEvent(reportTime);
        }
    }

    private void parseIgnitionAndCreateEvent(ReportContext reportCtx, String imei) {
        if (reportCtx.getEntry(4).equals(MOTION_DETECTED_IGNITION_ON)) {
            queclinkIgnition.createIgnitionOnEvent(reportCtx, imei);
        } else if (reportCtx.getEntry(4).equals(MOTION_DETECTED_IGNITION_OFF)) {
            queclinkIgnition.createIgnitionOffEvent(reportCtx, imei);
        }        
    }

    private void parseMotionStateAndCreateEvent(String[] report, String imei) {
        String deviceType = report[1].substring(0, 2);
        boolean motion; 
        if (report[0].equals(MOTION_REPORT[1])) {
            String motionState = report[5];
            motion = motionState.equals("1");

        }  else {
            String motionState = report[4];

            if(QueclinkConstants.GV500_ID1.equals(deviceType) || QueclinkConstants.GV500_ID2.equals(deviceType)) { // gv500
                motionState = report[5];
            }

            motion = MOTION_DETECTED.equals(motionState)
                    || MOTION_DETECTED_IGNITION_OFF.equals(motionState)
                    || MOTION_DETECTED_IGNITION_ON.equals(motionState)
                    || BEING_TOWED.equals(motionState);

        }


        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(imei);
        DateTime dateTime = queclinkReport.getReportDateTime(report);
        device.motionEvent(motion, dateTime);
    }

    private boolean onParsedMTrackingAck(String[] report, String imei) throws SDKException {
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

        synchronized(this) {
            if (returnedCommandSerialNum != corrId) {
                return false;
            }
            ackTracking = lastOperation.get(Tracking.class);
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
        String deviceType = deviceMo.getType();
        String password = new String();
        if(getQueclinkDevice().getDeviceByType(deviceType) != null) {
            password = getQueclinkDevice().getDeviceByType(deviceType).getDefaultPassword();
        }
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
            if (track != null && track.getInterval() > 0) {
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

        return deviceCommand;
    }
    
    private String setTrackingOptionsOnDevice(GId deviceId, Tracking tracking) {
        
        String deviceCommand = new String();
        
        ManagedObjectRepresentation deviceMo = getQueclinkDevice().getManagedObjectFromGId(deviceId);
        String deviceType = deviceMo.getType();
        String password = new String();
        if(getQueclinkDevice().getDeviceByType(deviceType) != null) {
            password = getQueclinkDevice().getDeviceByType(deviceType).getDefaultPassword();
        } 
        
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
