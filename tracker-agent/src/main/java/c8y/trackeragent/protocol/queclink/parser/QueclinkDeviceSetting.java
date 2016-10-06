package c8y.trackeragent.protocol.queclink.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.MotionTracking;
import c8y.trackeragent.TrackerAgent;
import c8y.Tracking;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.protocol.queclink.device.QueclinkDevice;
import c8y.trackeragent.tracker.Translator;

@Component
public class QueclinkDeviceSetting extends QueclinkParser implements Translator {
    
    private Logger logger = LoggerFactory.getLogger(QueclinkDeviceSetting.class);
    private final TrackerAgent trackerAgent;
    
    /**
     * Bitmask:
     * Report message when it detects non movement: 2
     * Report message when it detects movement: 4 
     * Change the fix interval and send interval of FRI to <rest fix interval> and <rest send interval> when it detects non-movement: 8
     */
    public static final String BITMASK_MODENOMOTION = "E"; // 2 + 4 + 8

    /**
     * Set report interval on no-motion state
     * Template parameters: password, rest fix interval, rest send interval, serial number
     */
    public static final String REPORT_INTERVAL_NO_MOTION_TEMPLATE = "AT+GTNMD=%s,%s,,,,%d,%d,,,,,,,,%04x$";
    
    public static final String REPORT_INTERVAL_NO_MOTION_ACK = "+ACK:GTNMD";
    
    private short commandSerialNum = 0;
    private OperationRepresentation lastOperation;
    
    @Autowired
    public QueclinkDeviceSetting(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
        queclinkDevice.setTrackerAgent(trackerAgent);
    }
    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        
        if (REPORT_INTERVAL_NO_MOTION_ACK.equals(reportCtx.getEntry(0))) { //or only check ack
            return onParsedAck(reportCtx, reportCtx.getImei());
        }
        else {
            return setDeviceInfo(reportCtx);
        }
    }

    private boolean onParsedAck(ReportContext reportCtx, String imei) {
        
        short returnedCommandSerialNum = Short.parseShort(reportCtx.getEntry(4), 16);
        Tracking ackTracking;
        
        synchronized(this) {
            if (returnedCommandSerialNum != commandSerialNum) {
                return false;
            }
            ackTracking = lastOperation.get(Tracking.class);   
        }
        
        try {
            // store fragment to managed object
            trackerAgent.getOrCreateTrackerDevice(imei).setTracking(ackTracking.getInterval());
            trackerAgent.finish(imei, lastOperation);
        
        } catch (SDKException sdkException) {
            trackerAgent.fail(imei, lastOperation, "Error setting tracking to managed object", sdkException);
        }
        
        return true;
    }
    private boolean setDeviceInfo(ReportContext reportCtx) {
        
        queclinkDevice.setProtocolVersion(reportCtx.getEntry(1));
        queclinkDevice.getOrUpdateTrackerDevice(reportCtx.getImei());
        
        return true;
        
    }
    @Override
    public String translate(OperationContext operationCtx) {
        
        OperationRepresentation operation = operationCtx.getOperation();
        Tracking tracking = operation.get(Tracking.class);
        
        if (tracking == null) {
            return null;
        }
        String device_command = new String();
       
        synchronized (this) {
            commandSerialNum++;
            lastOperation = operation;
        }
        
        if (tracking != null) {
            
            String password = queclinkDevice.getDevicePasswordFromGId(operation.getDeviceId());
            if (tracking.getInterval() >= 0) {
                int interval = tracking.getInterval();
                device_command = String.format(REPORT_INTERVAL_NO_MOTION_TEMPLATE, password, BITMASK_MODENOMOTION, interval, interval, commandSerialNum);
                
                // add restart command
                device_command += String.format("AT+GTRTO=%s,3,,,,,,0001$", password);
            }
        }
        
        
        //if no_motion_report interval is set
        //return REPORT_INTERVAL_NO_MOTION_TEMPLATE 
        
        //if on_motion_report interval is set
        //return REPORT_INTERVAL_ON_MOTION_TEMPLATE
        
        //or both    
        
        return (device_command.isEmpty())? null : device_command;
    }
    

}
