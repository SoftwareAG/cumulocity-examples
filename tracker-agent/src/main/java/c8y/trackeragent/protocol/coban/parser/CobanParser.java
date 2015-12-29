package c8y.trackeragent.protocol.coban.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.Parser;
import c8y.trackeragent.TrackerAgent;

import com.cumulocity.sdk.client.SDKException;

public abstract class CobanParser  extends CobanSupport implements Parser {
    
    private static final Logger logger = LoggerFactory.getLogger(CobanParser.class);
    
    public CobanParser(TrackerAgent trackerAgent) {
        super(trackerAgent);
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
}
