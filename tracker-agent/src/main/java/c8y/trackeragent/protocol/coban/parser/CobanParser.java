package c8y.trackeragent.protocol.coban.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.Parser;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;

import com.cumulocity.sdk.client.SDKException;

public abstract class CobanParser implements Parser {
    
    private static final Logger logger = LoggerFactory.getLogger(CobanParser.class);
    private static final String IMEI_PREFIX = "imei:";
    
    protected final TrackerAgent trackerAgent;
    
    public CobanParser(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
    }
    
    protected abstract boolean accept(String[] report);
    protected abstract String doParse(String[] report);
    
    @Override
    public final String parse(String[] report) throws SDKException {
        if (accept(report)) {
            String imei = doParse(report);
            logger.debug("Imei = '{}'", imei);
            return imei;
        } else {
            return null;
        }
    }
    
    protected String extractImeiValue(String imeiPart) {
        return imeiPart.replaceFirst(IMEI_PREFIX, "");
    }

    protected void writeOut(ReportContext reportCtx, String string) {
        try {
            reportCtx.getOut().write(string.getBytes("US-ASCII"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
