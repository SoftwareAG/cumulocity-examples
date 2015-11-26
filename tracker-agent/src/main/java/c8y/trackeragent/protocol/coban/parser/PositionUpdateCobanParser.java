package c8y.trackeragent.protocol.coban.parser;

import static c8y.trackeragent.utils.SignedLocation.altitude;
import static c8y.trackeragent.utils.SignedLocation.latitude;
import static c8y.trackeragent.utils.SignedLocation.longitude;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.MotionTracking;
import c8y.Position;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.Translator;
import c8y.trackeragent.operations.OperationContext;
import c8y.trackeragent.utils.message.TrackerMessage;
import c8y.trackeragent.utils.message.TrackerMessageFactory;

import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

public class PositionUpdateCobanParser extends CobanParser implements Translator {
    
    private static Logger logger = LoggerFactory.getLogger(PositionUpdateCobanParser.class);
    private final TrackerMessageFactory msgFactory;
    
    public PositionUpdateCobanParser(TrackerAgent trackerAgent, TrackerMessageFactory msgFactory) {
        super(trackerAgent);
        this.msgFactory = msgFactory;
    }

    @Override
    protected boolean accept(String[] report) {
        return report.length >= 1 && "001".equals(report[1]);
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
        logger.debug("Update position for IMEI {}.", reportCtx.getImei());
        BigDecimal lat = latitude().withValue(reportCtx.getEntry(7), reportCtx.getEntry(8)).getValue();
        BigDecimal lng = longitude().withValue(reportCtx.getEntry(9), reportCtx.getEntry(10)).getValue();
        BigDecimal alt = altitude().withValue(reportCtx.getEntry(11)).getValue();
        Position position = new Position();
        position.setLat(lat);
        position.setLng(lng);
        position.setAlt(alt);
        logger.debug("Update position for imei: {} to: {}.", reportCtx.getImei(), position);
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        device.setPosition(position);
        return true;
    }

    @Override
    public String translate(OperationContext operationCtx) {
        logger.debug("Translate operation {}.", operationCtx);
        OperationRepresentation operation = operationCtx.getOperation();
        MotionTracking mTrack = operation.get(MotionTracking.class);

        if (mTrack == null) {
            logger.debug("Skip. No fragment {}.", MotionTracking.class);
            return null;
        }
        
        if (!mTrack.isActive()) {
            logger.debug("Skip. Fragment {} inactive.", MotionTracking.class);
            return null;
        }
        
        String cobanRequest = (String) mTrack.getProperty("cobanRequest");
        if (cobanRequest == null) {
            logger.debug("Parsed message: {}", cobanRequest);
            return null;
        }
        
        String imeiPart = formatImeiValue(operationCtx.getImei());
        TrackerMessage msg = msg("**").appendField(msg(imeiPart)).appendField(msg(cobanRequest));
        return msg.asText();
    }
    
    private TrackerMessage msg(String text) {
        return msgFactory.message(text);
    }
    
}
