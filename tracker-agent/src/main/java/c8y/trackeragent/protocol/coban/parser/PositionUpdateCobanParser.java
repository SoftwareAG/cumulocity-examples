package c8y.trackeragent.protocol.coban.parser;

import static c8y.trackeragent.utils.SignedLocation.altitude;
import static c8y.trackeragent.utils.SignedLocation.latitude;
import static c8y.trackeragent.utils.SignedLocation.longitude;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Position;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;

import com.cumulocity.sdk.client.SDKException;

public class PositionUpdateCobanParser extends CobanParser {
    
    private static Logger logger = LoggerFactory.getLogger(PositionUpdateCobanParser.class);

    public PositionUpdateCobanParser(TrackerAgent trackerAgent) {
        super(trackerAgent);
    }

    @Override
    protected boolean accept(String[] report) {
        if(report.length < 1) {
            return false;
        }
        String reportType = report[1];
        return "001".equals(reportType);
    }

    @Override
    protected String doParse(String[] report) {
        return extractImeiValue(report[0]);
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        if (reportCtx.getNumberOfEntries() < 12) {
            logger.error("Invalid report: {}", reportCtx);
            return true;
        }
        logger.debug("Update position for imei {}.", reportCtx.getImei());
        BigDecimal lat = latitude().withValue(reportCtx.getEntry(7), reportCtx.getEntry(8)).getValue();
        BigDecimal lng = longitude().withValue(reportCtx.getEntry(9), reportCtx.getEntry(10)).getValue();
        BigDecimal alt = altitude().withValue(reportCtx.getEntry(11)).getValue();
        Position position = new Position();
        position.setLat(lat);
        position.setLng(lng);
        position.setAlt(alt);
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        device.setPosition(position);
        return true;
    }
    
}
