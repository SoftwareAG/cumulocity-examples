package c8y.trackeragent.protocol.queclink.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.sdk.client.SDKException;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.ManagedObjectCache;
import c8y.trackeragent.protocol.queclink.device.QueclinkDevice;

@Component
public class QueclinkDeviceSetting extends QueclinkParser {

    private Logger logger = LoggerFactory.getLogger(QueclinkDeviceSetting.class);
    private final TrackerAgent trackerAgent;
    private QueclinkDevice queclinkDevice = new QueclinkDevice();
    
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
}
