package c8y.trackeragent.protocol.coban.parser;

import static java.math.BigDecimal.ROUND_DOWN;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.Parser;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;

import com.cumulocity.sdk.client.SDKException;

public abstract class CobanParser  extends CobanSupport implements Parser {
    
    private static final Logger logger = LoggerFactory.getLogger(CobanParser.class);
    
    public static final BigDecimal COBAN_SPEED_MEASUREMENT_FACTOR = new BigDecimal(1.852);
    
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
    
    public static BigDecimal getSpeed(ReportContext reportCtx) {
        String entry = reportCtx.getEntry(11);
        if (entry == null) {
            logger.warn("There is no speed parameter in measurement");
            return null;
        }
        try {
            BigDecimal speedValue = new BigDecimal(entry);
            speedValue = speedValue.multiply(COBAN_SPEED_MEASUREMENT_FACTOR);
            return speedValue.setScale(0, ROUND_DOWN);
        } catch (NumberFormatException nfex) {
            logger.error("Wrong speed value: " + entry, nfex);
            return null;
        }
    }

}
