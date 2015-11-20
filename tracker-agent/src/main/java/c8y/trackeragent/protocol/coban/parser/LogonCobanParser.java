package c8y.trackeragent.protocol.coban.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;

import com.cumulocity.sdk.client.SDKException;

public class LogonCobanParser extends CobanParser {
    
    private static Logger logger = LoggerFactory.getLogger(LogonCobanParser.class);

    public LogonCobanParser(TrackerAgent trackerAgent) {
        super(trackerAgent);
    }
    
    @Override
    protected boolean accept(String[] report) {
        return report.length >= 2 && "##".equals(report[0]);
    }

    @Override
    public String doParse(String[] report) throws SDKException {
        return extractImeiValue(report[1]);
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        logger.debug("Success logon for imei {}.", reportCtx.getImei());
        writeOut(reportCtx, "LOAD"); 
        return true;
    }
    
    
    
    

}
