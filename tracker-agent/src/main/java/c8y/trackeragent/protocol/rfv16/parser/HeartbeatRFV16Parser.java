package c8y.trackeragent.protocol.rfv16.parser;

import java.math.BigDecimal;
import java.util.Collection;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.Parser;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.service.AlarmService;
import c8y.trackeragent.service.MeasurementService;

import com.cumulocity.sdk.client.SDKException;
import com.google.common.base.Strings;

/**
 * listen to HEARTBEAT (LINK) message
 *
 */
public class HeartbeatRFV16Parser extends RFV16Parser implements Parser {

    private static Logger logger = LoggerFactory.getLogger(HeartbeatRFV16Parser.class);
    
    private final AlarmService alarmService;
    private final MeasurementService measurementService;

    public HeartbeatRFV16Parser(TrackerAgent trackerAgent, 
            RFV16ServerMessages serverMessages, 
            AlarmService alarmService,
            MeasurementService measurementService
            ) {
        super(trackerAgent, serverMessages);
        this.alarmService = alarmService;
        this.measurementService = measurementService;
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        if (!isHeartbeat(reportCtx)) {
            return false;
        }        
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        ping(reportCtx, device);
        String status = reportCtx.getEntry(10);
        if (Strings.isNullOrEmpty(status)) {
            logger.debug("Invalid heartbeat message format (empty status) {}", reportCtx);
            return false;
        }
        logger.debug("Read status {} as alarms for device {}", status, reportCtx.getImei());
        Collection<RFV16AlarmType> alarmTypes = AlarmTypeDecoder.getAlarmTypes(status);
        logger.debug("Read status {} as alarms {} for device {}", status, reportCtx.getImei(), alarmTypes);
        for (RFV16AlarmType alarmType : alarmTypes) {
            alarmService.createAlarm(reportCtx, alarmType, device);
        }
        BigDecimal batteryLevel = getBatteryPercentageLevel(reportCtx);
        if (batteryLevel != null) {
            measurementService.createBatteryLevelMeasurement(batteryLevel, device, new DateTime());
        }
        BigDecimal gsmLevel = getGSMPercentageLevel(reportCtx);
        if (gsmLevel != null) {
            measurementService.createGSMLevelMeasurement(gsmLevel, device, new DateTime());
        }
        return true;
    }

    private boolean isHeartbeat(ReportContext reportCtx) {
        return RFV16Constants.MESSAGE_TYPE_LINK.equalsIgnoreCase(reportCtx.getEntry(2));
    }

    private void ping(ReportContext reportCtx, TrackerDevice device) {
        logger.debug("Heartbeat for imei {}.", reportCtx.getImei());
        try {
            device.ping();
        } catch (Exception ex) {
            logger.error("Error processing heartbeat on imei " +  reportCtx.getImei(), ex);
        }
    }
    
    private BigDecimal getBatteryPercentageLevel(ReportContext reportCtx) {
        return reportCtx.getEntryAsNumber(6);
    }
    
    private BigDecimal getGSMPercentageLevel(ReportContext reportCtx) {
        return reportCtx.getEntryAsNumber(4);
    }



}
