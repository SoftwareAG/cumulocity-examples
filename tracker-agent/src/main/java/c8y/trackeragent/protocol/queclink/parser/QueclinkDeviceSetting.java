package c8y.trackeragent.protocol.queclink.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.MotionTracking;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.ManagedObjectCache;
import c8y.trackeragent.protocol.queclink.device.QueclinkDevice;
import c8y.trackeragent.tracker.Translator;

@Component
public class QueclinkDeviceSetting extends QueclinkParser implements Translator {
    
    private Logger logger = LoggerFactory.getLogger(QueclinkDeviceSetting.class);
    private final TrackerAgent trackerAgent;
    private QueclinkDevice queclinkDevice = new QueclinkDevice();
    
    /**
     * Set report interval on motion state
     * Template parameters: password, send interval, serial number
     */
    public static final String REPORT_INTERVAL_ON_MOTION_TEMPLATE = "AT+GTFRI=%s,,,,,,,,%d,,,,,,,,,,,,,%04x$";
    /**
     * Set report interval on no-motion state
     * Template parameters: password, rest send interval, serial number
     */
    public static final String REPORT_INTERVAL_NO_MOTION_TEMPLATE = "AT+GTNMD=%s,%d,,,,,%d,,,,,,,,%04x$";
    
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
        MotionTracking mTrack = operation.get(MotionTracking.class);
        
        if (mTrack == null) {
            return null;
        }
        
        //if mTrack no_motion_report interval is set
        //return REPORT_INTERVAL_NO_MOTION_TEMPLATE 
        
        //if mTrack on_motion_report interval is set
        //return REPORT_INTERVAL_ON_MOTION_TEMPLATE
        
        return null;
    }
}
