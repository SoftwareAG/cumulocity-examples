package c8y.trackeragent.protocol.rfv16.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.Parser;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;

import com.cumulocity.sdk.client.SDKException;

public abstract class RFV16Parser implements Parser {
    
    private static final Logger logger = LoggerFactory.getLogger(RFV16Parser.class);
    
    protected final TrackerAgent trackerAgent;
    protected final RFV16ServerMessages serverMessages;
    
    public RFV16Parser(TrackerAgent trackerAgent, RFV16ServerMessages serverMessages) {
        this.trackerAgent = trackerAgent;
        this.serverMessages = serverMessages;
    }

    @Override
    public final String parse(String[] report) throws SDKException {
        if (accept(report)) {
            String imei = getImei(report);
            logger.debug("Imei = '{}'", imei);
            return imei;
        } else {
            return null;
        }
    }

    private boolean accept(String[] report) {
        return report.length > 1;
    }

    private String getImei(String[] report) {
        return report[1];
    }
}
