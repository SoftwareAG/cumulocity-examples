package c8y.trackeragent.protocol.coban.parser;

import static c8y.trackeragent.protocol.coban.message.CobanServerMessages.imeiMsg;
import static java.math.BigDecimal.valueOf;

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
import c8y.trackeragent.protocol.coban.CobanConstants;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.protocol.coban.service.AlarmService;
import c8y.trackeragent.utils.TK10xCoordinatesTranslator;
import c8y.trackeragent.utils.message.TrackerMessage;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

public class PositionUpdateCobanParser extends CobanParser implements Translator {
    
    private static Logger logger = LoggerFactory.getLogger(PositionUpdateCobanParser.class);
    
    private static final String KEYWORD = "tracker";
    
    private final CobanServerMessages serverMessages;
    private final AlarmService alarmService;
    
    public PositionUpdateCobanParser(TrackerAgent trackerAgent, 
            CobanServerMessages serverMessages, AlarmService alarmService) {
        super(trackerAgent);
        this.serverMessages = serverMessages;
        this.alarmService = alarmService;
    }

    @Override
    protected boolean accept(String[] report) {
        return report.length >= 1 && KEYWORD.equals(report[1]);
    }

    @Override
    protected String doParse(String[] report) {
        return CobanServerMessages.extractImeiValue(report[0]);
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        if (CobanConstants.GPS_KO.equals(reportCtx.getEntry(4))) {
            logger.error("NO GPS signal in report: {}, ignore!", reportCtx);
            alarmService.createAlarm(reportCtx, AlarmType.NO_GPS_SIGNAL, device);
            return true;            
        }
        if (reportCtx.getNumberOfEntries() < 12) {
            logger.error("Invalid report: {}", reportCtx);
            return true;
        }
        double lat = TK10xCoordinatesTranslator.parseLatitude(reportCtx.getEntry(7), reportCtx.getEntry(8));
        double lng = TK10xCoordinatesTranslator.parseLongitude(reportCtx.getEntry(9), reportCtx.getEntry(10));
        Position position = new Position();
        position.setLat(valueOf(lat));
        position.setLng(valueOf(lng));
        position.setAlt(BigDecimal.ZERO);
        logger.debug("Update position for imei: {} to: {}.", reportCtx.getImei(), position);
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
        
        String imeiMsg = imeiMsg(operationCtx.getImei());
        TrackerMessage msg = serverMessages.msg().appendField("**").appendField(imeiMsg).appendField(cobanRequest);
        operation.setStatus(OperationStatus.SUCCESSFUL.toString());
        operation.set(msg.asText(), OPERATION_FRAGMENT_SERVER_COMMAND);
        return msg.asText();
    }
    
}
