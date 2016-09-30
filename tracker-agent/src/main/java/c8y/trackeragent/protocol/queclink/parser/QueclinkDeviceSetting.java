package c8y.trackeragent.protocol.queclink.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.Command;
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
    public static final String REPORT_INTERVAL_ON_MOTION_TEMPLATE = "AT+GTFRI=%s,,,,,,,,%s,,,,,,,,,,,,,0002$";
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
        
        queclinkDevice.setProtocolVersion(reportCtx.getEntry(1));
        queclinkDevice.getOrUpdateTrackerDevice(reportCtx.getImei());
        
        return queclinkDevice;
        
    }
    @Override
    public String translate(OperationContext operationCtx) {
        OperationRepresentation operation = operationCtx.getOperation();
        Command command = operation.get(Command.class);
        
        if (command == null || command.getText().isEmpty()) {
            return null;
        }
        
        
        //if no_motion_report interval is set
        //return REPORT_INTERVAL_NO_MOTION_TEMPLATE 
        
        //if on_motion_report interval is set
        //return REPORT_INTERVAL_ON_MOTION_TEMPLATE
        
        //or both
        
        
        
        String commandText = command.getText();
        String[] commandArr = commandText.split("\n");
        String device_command = new String();
        
        //TODO check why two concatenated commands gives failure on agent
        for(int i = 0; i < commandArr.length; ++i) {
            
            String configKeyandValue[] = commandArr[i].split(" ");
            if (configKeyandValue[0].trim().equals("reportNoMotion")) {
                if(!configKeyandValue[1].trim().isEmpty()) {
                    device_command += String.format(REPORT_INTERVAL_NO_MOTION_TEMPLATE, "gl300", BITMASK_MODENOMOTION, configKeyandValue[1].trim());
                }
            }
            
            if (configKeyandValue[0].trim().equals("reportOnMotion")) {
                if(!configKeyandValue[1].trim().isEmpty()) {
                    device_command += String.format(REPORT_INTERVAL_ON_MOTION_TEMPLATE, PASSWORD, configKeyandValue[1].trim());
                }
            }
        }
        
        
        return (device_command.isEmpty())? null : device_command;
    }
    

}
