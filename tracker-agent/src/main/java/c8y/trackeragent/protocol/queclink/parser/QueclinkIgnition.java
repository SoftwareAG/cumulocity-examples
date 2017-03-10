package c8y.trackeragent.protocol.queclink.parser;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.sdk.client.SDKException;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;

@Component
public class QueclinkIgnition extends QueclinkParser {
    
    public static final String IGNITION_ON = "+RESP:GTIDN";
    public static final String IGNITION_OFF = "+RESP:GTIDF";
    
    private final TrackerAgent trackerAgent;
    
    @Autowired
    public QueclinkIgnition(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
    }
    
    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        String reportType = reportCtx.getReport()[0];
        if (reportType.equals(IGNITION_ON)) {
            createIgnitionOnEvent(reportCtx, reportCtx.getImei());
            return true;
        } else if (reportType.equals(IGNITION_OFF)) {
            createIgnitionOffEvent(reportCtx, reportCtx.getImei());
            return true;
        }
        return false;
    }
    
    private void createIgnitionOffEvent(ReportContext reportCtx, String imei) {
        TrackerDevice trackerDevice = trackerAgent.getOrCreateTrackerDevice(imei);
        DateTime dateTime = getQueclinkDevice().getReportDateTime(reportCtx.getReport());
        trackerDevice.ignitionOffEvent(dateTime);
        
    }
    

    private void createIgnitionOnEvent(ReportContext reportCtx, String imei) {
        TrackerDevice trackerDevice = trackerAgent.getOrCreateTrackerDevice(imei);
        DateTime dateTime = getQueclinkDevice().getReportDateTime(reportCtx.getReport());
        trackerDevice.ignitionOnEvent(dateTime);
        
    }

    public void getIgnitionFromMotionReport() {
        // TODO Auto-generated method stub
        
    }
    
}