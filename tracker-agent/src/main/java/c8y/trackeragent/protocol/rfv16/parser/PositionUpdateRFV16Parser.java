package c8y.trackeragent.protocol.rfv16.parser;

import static c8y.trackeragent.protocol.rfv16.RFV16Constants.CONNECTION_PARAM_CONTROL_COMMANDS_SENT;
import static c8y.trackeragent.utils.LocationEventBuilder.aLocationEvent;
import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.Position;
import c8y.SpeedMeasurement;
import c8y.trackeragent.Parser;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.service.AlarmService;
import c8y.trackeragent.service.MeasurementService;
import c8y.trackeragent.utils.LocationEventBuilder;
import c8y.trackeragent.utils.TK10xCoordinatesTranslator;
import c8y.trackeragent.utils.TrackerConfiguration;
import c8y.trackeragent.utils.message.TrackerMessage;

@Component
public class PositionUpdateRFV16Parser extends RFV16Parser implements Parser {

    private static Logger logger = LoggerFactory.getLogger(PositionUpdateRFV16Parser.class);

    private final MeasurementService measurementService;
    private final TrackerConfiguration config;

    @Autowired
    public PositionUpdateRFV16Parser(TrackerAgent trackerAgent, RFV16ServerMessages serverMessages,
            MeasurementService measurementService, AlarmService alarmService, TrackerConfiguration config) {
        super(trackerAgent, serverMessages, alarmService);
        this.measurementService = measurementService;
        this.config = config;
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        if (!isV1Report(reportCtx)) {
            return false;
        }
        if (isValidLocationData(reportCtx)) {
            logger.debug("Process V1 report", reportCtx);
            processValidPositionReport(reportCtx);
        } else {
            processInvalidPositionReport(reportCtx);
            logger.debug("Not valid position report: {}", reportCtx);

        }
        return true;
    }
    
    private void processValidPositionReport(ReportContext reportCtx) {
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        Collection<AlarmRepresentation> alarms = createAlarms(reportCtx, device, reportCtx.getEntry(12));
        double lat = TK10xCoordinatesTranslator.parseLatitude(reportCtx.getEntry(5), reportCtx.getEntry(6));
        double lng = TK10xCoordinatesTranslator.parseLongitude(reportCtx.getEntry(7), reportCtx.getEntry(8));
        SpeedMeasurement speed = createSpeedMeasurement(reportCtx, device);
        // @formatter:off
        LocationEventBuilder locationEvent = aLocationEvent()
        		.withSourceId(device.getGId())
                .withLat(valueOf(lat))
                .withLng(valueOf(lng))
                .withAlt(BigDecimal.ZERO)
                .withSpeedMeasurement(speed)
                .withAlarms(alarms);
        // @formatter:on
        device.setPosition(locationEvent.build());
        if (!reportCtx.isConnectionFlagOn(CONNECTION_PARAM_CONTROL_COMMANDS_SENT)) {
            sendControllCommands(reportCtx);
        }
    }

    private void processInvalidPositionReport(ReportContext reportCtx) {
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        Collection<AlarmRepresentation> alarms = createAlarms(reportCtx, device, reportCtx.getEntry(12));
        if (alarms.isEmpty()) {
            return;
        }
        Position lastPosition = device.getLastPosition();
        if (lastPosition == null) {
            return;
        }
        SpeedMeasurement speed = createSpeedMeasurement(reportCtx, device);
        LocationEventBuilder locationEvent = aLocationEvent()
        		.withSourceId(device.getGId())
        		.withPosition(lastPosition)
        		.withAlarms(alarms)
        		.withSpeedMeasurement(speed);
        device.setPosition(locationEvent.build());
    }

    private SpeedMeasurement createSpeedMeasurement(ReportContext reportCtx, TrackerDevice device) {
        BigDecimal speedValue = getSpeed(reportCtx);
        if (speedValue == null) {
            return null;
        } else {
            return measurementService.createSpeedMeasurement(speedValue, device);
        }
    }

    private void sendControllCommands(ReportContext reportCtx) {
        TrackerMessage reportMonitoringCommand = serverMessages.reportMonitoringCommand(reportCtx.getImei(),
                config.getRfv16LocationReportTimeInterval().toString());
        reportCtx.writeOut(reportMonitoringCommand);
    }

    private boolean isV1Report(ReportContext reportCtx) {
        return reportCtx.getNumberOfEntries() == 13 && RFV16Constants.MESSAGE_TYPE_V1.equals(reportCtx.getEntry(2));
    }

    private boolean isValidLocationData(ReportContext reportCtx) {
        return RFV16Constants.DATE_EFFECTIVE_MARK.equals(reportCtx.getEntry(4));
    }
}
