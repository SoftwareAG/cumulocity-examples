package c8y.trackeragent.protocol.rfv16.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Position;
import c8y.SpeedMeasurement;
import c8y.trackeragent.Parser;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.protocol.rfv16.device.RFV16Device;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.service.AlarmService;
import c8y.trackeragent.service.MeasurementService;
import c8y.trackeragent.utils.message.TrackerMessage;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.SDKException;

public class PositionUpdateRFV16Parser extends RFV16Parser implements Parser {
    
    private static Logger logger = LoggerFactory.getLogger(PositionUpdateRFV16Parser.class);
    
    private final MeasurementService measurementService;
    private final AlarmService alarmService;
    
    public PositionUpdateRFV16Parser(TrackerAgent trackerAgent, RFV16ServerMessages serverMessages, 
            MeasurementService measurementService, AlarmService alarmService) {
        super(trackerAgent, serverMessages);
        this.measurementService = measurementService;
        this.alarmService = alarmService;
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        if (!isV1Report(reportCtx)) {
            return false;
        }
        if(!RFV16Constants.DATE_EFFECTIVE_MARK.equals(reportCtx.getEntry(4))) {
            logger.debug("Not valid position report: {}", reportCtx);
            return true;
        }
        logger.debug("Process V1 report", reportCtx);
        processPositionReport(reportCtx);
        return true;
    }

    private void processPositionReport(ReportContext reportCtx) {
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        Position position = getPosition(reportCtx);
        logger.debug("Update position for imei: {} to: {}.", reportCtx.getImei(), position);
        EventRepresentation event = device.aLocationUpdateEvent();
        BigDecimal speedValue = getSpeed(reportCtx);
        if (speedValue != null) {
            SpeedMeasurement speed = measurementService.createSpeedMeasurement(speedValue, device);
            event.set(speed);
        } 
        
        String status = reportCtx.getEntry(12);
        Collection<AlarmRepresentation> alarms = new ArrayList<AlarmRepresentation>();
        Collection<RFV16AlarmType> alarmTypes = AlarmTypeDecoder.getAlarmTypes(status);
        logger.debug("Read status {} as alarms {} for device {}", status, reportCtx.getImei(), alarmTypes);
        for (RFV16AlarmType alarmType : alarmTypes) {
            AlarmRepresentation alarm = alarmService.createRFV16Alarm(reportCtx, alarmType, device);
            alarms.add(alarm);
        }
        
        if (!alarms.isEmpty()) {
            alarmService.populateLocationEventByAlarms(event, alarms);
        }
        
        device.setPosition(event, position);            
        RFV16Device rfv16Device = getRFV16Device(reportCtx.getImei());
        String maker = reportCtx.getEntry(0);
        TrackerMessage timeIntervalLocationRequest = serverMessages.timeIntervalLocationRequest(maker, reportCtx.getImei(), rfv16Device.getLocationReportInterval());
        TrackerMessage turnOnAllAlarms = serverMessages.turnOnAllAlarms(maker, reportCtx.getImei());
        reportCtx.writeOut(timeIntervalLocationRequest.appendReport(turnOnAllAlarms));
    }

    private boolean isV1Report(ReportContext reportCtx) {
        return reportCtx.getNumberOfEntries() == 13 
                && RFV16Constants.MESSAGE_TYPE_V1.equals(reportCtx.getEntry(2))
                && RFV16Constants.DATE_EFFECTIVE_MARK.equals(reportCtx.getEntry(4));
    }
}    
