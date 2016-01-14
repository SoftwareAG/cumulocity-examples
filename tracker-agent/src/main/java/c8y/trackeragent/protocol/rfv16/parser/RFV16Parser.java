package c8y.trackeragent.protocol.rfv16.parser;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.Parser;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.protocol.rfv16.device.RFV16Device;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;

import com.cumulocity.sdk.client.SDKException;
import com.google.common.base.Strings;

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
    
    public static BigDecimal getSpeed(ReportContext reportCtx) {
        String entry = reportCtx.getEntry(9);
        if (Strings.isNullOrEmpty(entry)) {
            logger.warn("There is no speed parameter in measurement");
            return null;
        }
        try {
            return new BigDecimal(entry);
        } catch (NumberFormatException nfex) {
            logger.error("Wrong speed value: " + entry, nfex);
            return null;
        }
    }
    
    protected RFV16Device getRFV16Device(String imei) {
        return trackerAgent.getOrCreateTrackerDevice(imei).getRFV16Device();
    }

}
