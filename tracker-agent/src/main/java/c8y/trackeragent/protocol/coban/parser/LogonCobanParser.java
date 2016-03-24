package c8y.trackeragent.protocol.coban.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.protocol.coban.device.CobanDevice;
import c8y.trackeragent.protocol.coban.device.CobanDeviceFactory;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.utils.message.TrackerMessage;

import com.cumulocity.sdk.client.SDKException;

@Component
public class LogonCobanParser extends CobanParser {
    
    private static Logger logger = LoggerFactory.getLogger(LogonCobanParser.class);
    
    private CobanServerMessages serverMessages;

    @Autowired
    public LogonCobanParser(TrackerAgent trackerAgent, CobanServerMessages serverMessages) {
        super(trackerAgent);
        this.serverMessages = serverMessages;
    }
    
    @Override
    protected boolean accept(String[] report) {
        return report.length >= 2 && "##".equals(report[0]);
    }

    @Override
    public String doParse(String[] report) throws SDKException {
        return CobanServerMessages.extractImeiValue(report[1]);
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        logger.debug("Success logon for imei {}.", reportCtx.getImei());
        TrackerDevice device = getTrackerDevice(reportCtx.getImei());
        CobanDevice cobanDevice = device.getCobanDevice();
        TrackerMessage load = serverMessages.load();
        TrackerMessage positionReportsRequest = serverMessages.timeIntervalLocationRequest(reportCtx.getImei(), getLocationReportInterval(device, cobanDevice));
        reportCtx.writeOut(load.appendReport(positionReportsRequest)); 
        return true;
    }

    private String getLocationReportInterval(TrackerDevice device, CobanDevice cobanDevice) {
        Integer locationReportInterval = device.getUpdateIntervalProvider().findUpdateInterval();
        if (locationReportInterval == null) {
            return cobanDevice.getLocationReportInterval();
        } else {
            return CobanDeviceFactory.formatLocationReportInterval(locationReportInterval);
        }
    }
}
