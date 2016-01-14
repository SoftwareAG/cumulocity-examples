package c8y.trackeragent.protocol.rfv16.parser;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Position;
import c8y.SpeedMeasurement;
import c8y.trackeragent.Parser;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.coban.service.MeasurementService;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.utils.TK10xCoordinatesTranslator;

import com.cumulocity.sdk.client.SDKException;

public class PositionUpdateRFV16Parser extends RFV16Parser implements Parser {
    
    private static Logger logger = LoggerFactory.getLogger(PositionUpdateRFV16Parser.class);
    
    private final MeasurementService measurementService;
    
    public PositionUpdateRFV16Parser(TrackerAgent trackerAgent, RFV16ServerMessages serverMessages, MeasurementService measurementService) {
        super(trackerAgent, serverMessages);
        this.measurementService = measurementService;
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        boolean v1 = isValidV1(reportCtx);
        if (!v1) {
            logger.debug("Not valid v1 report: {}", reportCtx);
            return true;
        }
        double lat = TK10xCoordinatesTranslator.parseLatitude(reportCtx.getEntry(5), reportCtx.getEntry(6));
        double lng = TK10xCoordinatesTranslator.parseLongitude(reportCtx.getEntry(7), reportCtx.getEntry(8));
        Position position = new Position();
        position.setLat(valueOf(lat));
        position.setLng(valueOf(lng));
        position.setAlt(BigDecimal.ZERO);
        logger.debug("Update position for imei: {} to: {}.", reportCtx.getImei(), position);
        SpeedMeasurement speed = measurementService.createSpeedMeasurement(reportCtx, device);
        device.setPositionAndSpeed(position, speed);
        return true;
    }

    private boolean isValidV1(ReportContext reportCtx) {
        return reportCtx.getNumberOfEntries() == 13 
                && reportCtx.getEntry(2) == RFV16Constants.MESSAGE_TYPE_V1
                && reportCtx.getEntry(4) == RFV16Constants.DATE_EFFECTIVE_MARK;
    }

}
