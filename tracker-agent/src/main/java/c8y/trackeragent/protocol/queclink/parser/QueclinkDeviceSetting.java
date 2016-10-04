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
    public static final String REPORT_INTERVAL_NO_MOTION_TEMPLATE = "AT+GTNMD=%s,%s,,,,10,%d,,,,,,,,0032$";
    
    public static final String REPORT_INTERVAL_NO_MOTION_ACK = "+ACK:GTNMD";
    @Autowired
    public QueclinkDeviceSetting(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
        queclinkDevice.setTrackerAgent(trackerAgent);
    }
    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        
        setDeviceInfo(reportCtx);
       
        return true;
    }

    private QueclinkDevice setDeviceInfo(ReportContext reportCtx) {
        
        queclinkDevice.setProtocolVersion(reportCtx.getEntry(1));
        queclinkDevice.getOrUpdateTrackerDevice(reportCtx.getImei());
        
        return queclinkDevice;
        
    }
    @Override
    public String translate(OperationContext operationCtx) {
        
        OperationRepresentation operation = operationCtx.getOperation();
        Tracking tracking = operation.get(Tracking.class);
        //MotionTracking m_tracking = operation.get(MotionTracking.class);
        
        String device_command = new String();
        
        if (tracking != null) {
            
            String password = queclinkDevice.getDevicePasswordFromGId(operation.getDeviceId());
            if (tracking.getInterval() != 0) {
                int interval = tracking.getInterval();
                device_command = String.format(REPORT_INTERVAL_NO_MOTION_TEMPLATE, password, BITMASK_MODENOMOTION, interval);
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
