package c8y.trackeragent.protocol.coban.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.protocol.coban.device.CobanDevice;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.utils.message.TrackerMessage;

import com.cumulocity.sdk.client.SDKException;

public class LogonCobanParser extends CobanParser {
    
    private static Logger logger = LoggerFactory.getLogger(LogonCobanParser.class);
    
    private CobanServerMessages serverMessages;

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
        CobanDevice cobanDevice = getCobanDevice(reportCtx.getImei());
        TrackerMessage load = serverMessages.load();
        TrackerMessage positionReportsRequest = serverMessages.timeIntervalLocationRequest(reportCtx.getImei(), cobanDevice.getLocationReportInterval());
        reportCtx.writeOut(load.appendReport(positionReportsRequest)); 
        return true;
    }
}
