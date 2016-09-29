package c8y.trackeragent.protocol.queclink.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.Configuration;
import c8y.MotionTracking;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.ManagedObjectCache;
import c8y.trackeragent.protocol.queclink.QueclinkConstants;
import c8y.trackeragent.protocol.queclink.device.QueclinkDevice;
import c8y.trackeragent.tracker.Translator;

@Component
public class QueclinkDeviceSetting extends QueclinkParser implements Translator {
    
    private Logger logger = LoggerFactory.getLogger(QueclinkDeviceSetting.class);
    private final TrackerAgent trackerAgent;
    private QueclinkDevice queclinkDevice = new QueclinkDevice();
    
    /**
     * Bitmask:
     * Report message when it detects non movement: 2
     * Report message when it detects movement: 4 
     * Change the fix interval and send interval of FRI to <rest fix interval> and <rest send interval> when it detects non-movement: 8
     */
    public static final int BITMASK_MODENOMOTION = 2 + 4 + 8;
    /**
     * Set report interval on motion state
     * Template parameters: password, send interval, serial number
     */
    public static final String REPORT_INTERVAL_ON_MOTION_TEMPLATE = "AT+GTFRI=%s,,,,,,,,%s,,,,,,,,,,,,,0001$";
    /**
     * Set report interval on no-motion state
     * Template parameters: password, rest send interval, serial number
     */
    public static final String REPORT_INTERVAL_NO_MOTION_TEMPLATE = "AT+GTNMD=%s,%d,,,,,%s,,,,,,,,0001$";
    
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
        
        String imei = reportCtx.getImei();
        String protocolVersion = reportCtx.getEntry(1);
        String type = protocolVersion.substring(0, 2);
        String revision = protocolVersion.substring(2, 4) + "." + protocolVersion.substring(4, 6);
        
        queclinkDevice.setType(type);
        queclinkDevice.setRevision(revision);
        queclinkDevice.getOrUpdateTrackerDevice(reportCtx.getImei());
        
        return queclinkDevice;
        
    }
    @Override
    public String translate(OperationContext operationCtx) {
        OperationRepresentation operation = operationCtx.getOperation();
        Configuration conf = operation.get(Configuration.class);
        
        if (conf == null) {
            return null;
        }
        
        
        //if no_motion_report interval is set
        //return REPORT_INTERVAL_NO_MOTION_TEMPLATE 
        
        //if on_motion_report interval is set
        //return REPORT_INTERVAL_ON_MOTION_TEMPLATE
        
        //or both
        
        operation.setStatus(OperationStatus.SUCCESSFUL.toString());
        String configurationString = conf.getConfig();
        String[] configArr = configurationString.split("\n");
        String command = new String();
        
        //TODO check why two concatenated commands gives failure on agent
        for(int i = 0; i < configArr.length; ++i) {
            
            String configKeyandValue[] = configArr[i].split("=");
            if (configKeyandValue[0].trim().equals("c8y.reportInterval.noMotion")) {
                if(!configKeyandValue[1].trim().isEmpty()) {
                    command += String.format(REPORT_INTERVAL_NO_MOTION_TEMPLATE, "glpass", BITMASK_MODENOMOTION, configKeyandValue[1].trim());
                }
            }
            
            if (configKeyandValue[0].trim().equals("c8y.reportInterval.onMotion")) {
                if(!configKeyandValue[1].trim().isEmpty()) {
                    //command += String.format(REPORT_INTERVAL_NO_MOTION_TEMPLATE, "glpass", configKeyandValue[1].trim());
                }
            }
        }
        
        
        return (command.isEmpty())? null : command;
    }
}
