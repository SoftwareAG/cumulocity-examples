package c8y.trackeragent.protocol.queclink.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.MotionTracking;
import c8y.trackeragent.TrackerAgent;
import c8y.Tracking;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.protocol.queclink.QueclinkConstants;
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
    public static final String[] REPORT_INTERVAL_NO_MOTION_TEMPLATE = {
            "AT+GTNMD=%s,%s,,,,%d,%d,,,,,,,,%04x$", // supported on gl200, gl300
            "AT+GTNMD=%s,%s,,,,%d,,,,%04x$" // supported on gl50x
            };
    
    public static final String REPORT_INTERVAL_NO_MOTION_ACK = "+ACK:GTNMD";
    
    private short commandSerialNum = 1;
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
            //trackerAgent.finish(imei, lastOperation); //{error="devicecontrol/Not Found",message="Finding device data from database failed : No operation for gid '<operation id>'!",info="https://www.cumulocity.com/guides/reference-guide/#error_reporting",details="{exceptionMessage="Finding device data from database failed"
        
        } catch (SDKException sdkException) {
            trackerAgent.fail(imei, lastOperation, "Error setting tracking to managed object", sdkException);
        }
        
        return true;
    }
    private boolean setDeviceInfo(ReportContext reportCtx) {
        
        queclinkDevice.getOrUpdateTrackerDevice(reportCtx.getEntry(1), reportCtx.getImei());
        
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

        ManagedObjectRepresentation deviceMo = queclinkDevice.getManagedObjectFromGId(operation.getDeviceId());
        String password = (String) deviceMo.get("password");
        
        if (tracking.getInterval() >= 0) {
            int interval = tracking.getInterval();
            
            if (queclinkDevice.convertDeviceTypeToQueclinkType(QueclinkConstants.GL500_ID).equals(deviceMo.getType()) ||
                    queclinkDevice.convertDeviceTypeToQueclinkType(QueclinkConstants.GL505_ID).equals(deviceMo.getType())) {
                
                int intervalInMins = interval / 60;
                device_command = String.format(REPORT_INTERVAL_NO_MOTION_TEMPLATE[1], password, BITMASK_MODENOMOTION, intervalInMins, commandSerialNum);

            } else {
                device_command = String.format(REPORT_INTERVAL_NO_MOTION_TEMPLATE[0], password, BITMASK_MODENOMOTION, interval, interval, commandSerialNum);
            }
            
            // add restart command
            device_command += String.format("AT+GTRTO=%s,3,,,,,,0001$", password);
        }

        return (device_command.isEmpty())? null : device_command;
    }
    

}
