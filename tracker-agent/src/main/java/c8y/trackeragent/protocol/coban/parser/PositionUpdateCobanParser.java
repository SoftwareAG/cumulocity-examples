package c8y.trackeragent.protocol.coban.parser;

import static c8y.trackeragent.protocol.coban.message.CobanServerMessages.imeiMsg;
import static c8y.trackeragent.utils.LocationEventBuilder.aLocationEvent;
import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.MotionTracking;
import c8y.SpeedMeasurement;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.Translator;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.protocol.coban.CobanConstants;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.service.AlarmService;
import c8y.trackeragent.service.MeasurementService;
import c8y.trackeragent.utils.LocationEventBuilder;
import c8y.trackeragent.utils.TK10xCoordinatesTranslator;
import c8y.trackeragent.utils.message.TrackerMessage;

@Component
public class PositionUpdateCobanParser extends CobanParser implements Translator {
    
    private static Logger logger = LoggerFactory.getLogger(PositionUpdateCobanParser.class);
    
    private static final String KEYWORD = "tracker";
    
    private final CobanServerMessages serverMessages;
    private final AlarmService alarmService;
    private final MeasurementService measurementService;

    @Autowired
    public PositionUpdateCobanParser(TrackerAgent trackerAgent, 
            CobanServerMessages serverMessages, 
            AlarmService alarmService, 
            MeasurementService measurementService) {
        super(trackerAgent);
        this.serverMessages = serverMessages;
        this.alarmService = alarmService;
        this.measurementService = measurementService;
    }

    @Override
    protected boolean accept(String[] report) {
	if (report.length <= 1) {
	    return false;
	}
	boolean position = KEYWORD.equals(report[1]);
	boolean alarm = getAlarmType(report) != null;
        return position || alarm;
    }

    @Override
    protected String doParse(String[] report) {
        return CobanServerMessages.extractImeiValue(report[0]);
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        AlarmRepresentation alarm = null;
        if (CobanConstants.GPS_KO.equals(reportCtx.getEntry(4))) {
            logger.error("NO GPS signal in report: {}, ignore!", reportCtx);
            alarm = alarmService.createAlarm(reportCtx, CobanAlarmType.NO_GPS_SIGNAL, device);
            return true;            
        }
        if (reportCtx.getNumberOfEntries() < 12) {
            logger.error("Invalid report: {}", reportCtx);
            return true;
        }
        if (CobanConstants.GPS_OK.equals(reportCtx.getEntry(4))) {
            alarmService.clearAlarm(reportCtx, CobanAlarmType.NO_GPS_SIGNAL, device);
        }

        CobanAlarmType alarmType = getAlarmType(reportCtx.getReport());
        if (alarmType != null) {
            logger.info("Process alarm {} for imei {}.", alarmType, reportCtx.getImei());
            alarm = alarmService.createAlarm(reportCtx, alarmType, device);
        }
        LocationEventBuilder aLocationEvent = aLocationEvent();
        
        double lat = TK10xCoordinatesTranslator.parseLatitude(reportCtx.getEntry(7), reportCtx.getEntry(8));
        double lng = TK10xCoordinatesTranslator.parseLongitude(reportCtx.getEntry(9), reportCtx.getEntry(10));
        aLocationEvent.withLat(valueOf(lat)).withLng(valueOf(lng)).withAlt(BigDecimal.ZERO);
        
        BigDecimal speedValue = getSpeed(reportCtx);
        if (speedValue != null) {
        	logger.debug("Parsed speed for imei: {} to: {}.", reportCtx.getImei(), speedValue);
            SpeedMeasurement speed = measurementService.createSpeedMeasurement(speedValue, device);
            aLocationEvent.withSpeedMeasurement(speed);
        }
        if (alarm != null) {
            aLocationEvent.withAlarm(alarm);
        }
        device.setPosition(aLocationEvent.build());
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
    
    public CobanAlarmType getAlarmType(String[] report) {
        for (CobanAlarmType alarmType : CobanAlarmType.values()) {
            if (alarmType.accept(report)) {
                return alarmType;
            }
        }
        return null;
    }
    
}
