package c8y.trackeragent.protocol.rfv16.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.Parser;

import com.cumulocity.sdk.client.SDKException;

public abstract class RFV16Parser implements Parser {
    
    private static final Logger logger = LoggerFactory.getLogger(RFV16Parser.class);
    
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

    private boolean accept(String[] report) {
        return report.length > 1;
    }

    private String doParse(String[] report) {
        return report[0];
    }
}
