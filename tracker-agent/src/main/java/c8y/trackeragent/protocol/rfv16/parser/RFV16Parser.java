package c8y.trackeragent.protocol.rfv16.parser;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Position;
import c8y.trackeragent.Parser;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.protocol.rfv16.device.RFV16Device;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.utils.TK10xCoordinatesTranslator;

import com.cumulocity.sdk.client.SDKException;
import com.google.common.base.Strings;

public abstract class RFV16Parser implements Parser, RFV16Fragment {
    
    private static final Logger logger = LoggerFactory.getLogger(RFV16Parser.class);
    
    protected final TrackerAgent trackerAgent;
    protected final RFV16ServerMessages serverMessages;
    
    public RFV16Parser(TrackerAgent trackerAgent, RFV16ServerMessages serverMessages) {
        this.trackerAgent = trackerAgent;
        this.serverMessages = serverMessages;
    }

    @Override
    public final String parse(String[] report) throws SDKException {
        if (report.length > 1) {
            String imei = report[1];
            logger.debug("Imei = '{}'", imei);
            return imei;
        } else {
            return null;
        }
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
    
    protected Position getPosition(ReportContext reportCtx) {
        double lat = TK10xCoordinatesTranslator.parseLatitude(reportCtx.getEntry(5), reportCtx.getEntry(6));
        double lng = TK10xCoordinatesTranslator.parseLongitude(reportCtx.getEntry(7), reportCtx.getEntry(8));
        Position position = new Position();
        position.setLat(valueOf(lat));
        position.setLng(valueOf(lng));
        position.setAlt(BigDecimal.ZERO);
        return position;
    }
    
    protected RFV16Device getRFV16Device(String imei) {
        return trackerAgent.getOrCreateTrackerDevice(imei).getRFV16Device();
    }
    
    public static DateTime getDateTime(ReportContext reportCtx) {
        DateTime time = RFV16ServerMessages.HHMMSS.parseDateTime(reportCtx.getEntry(3));
        DateTime date = RFV16ServerMessages.DDMMYY.parseDateTime(reportCtx.getEntry(11));
        return time.withDate(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
    }

}
