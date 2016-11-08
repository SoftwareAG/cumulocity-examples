package c8y.trackeragent.protocol.rfv16.parser;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.sdk.client.SDKException;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.service.AlarmService;
import c8y.trackeragent.service.MeasurementService;
import c8y.trackeragent.tracker.Parser;

/**
 * listen to response on CK server command
 *
 */
@Component
public class DeviceSituationRFV16Parser extends RFV16Parser implements Parser {

    private static Logger logger = LoggerFactory.getLogger(DeviceSituationRFV16Parser.class);
    
    private final MeasurementService measurementService;

    @Autowired
    public DeviceSituationRFV16Parser(TrackerAgent trackerAgent, 
            RFV16ServerMessages serverMessages, 
            MeasurementService measurementService,
            AlarmService alarmService
            ) {
        super(trackerAgent, serverMessages, alarmService);
        this.measurementService = measurementService;
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        if (!isDeviceSituation(reportCtx)) {
            return false;
        }
        logger.info("Received response on CK command: {}", reportCtx);
        TrackerDevice device = getTrackerDevice(reportCtx.getImei());

        BigDecimal batteryLevel = getBatteryPercentageLevel(reportCtx);
        if (batteryLevel != null) {
            measurementService.createPercentageBatteryLevelMeasurement(batteryLevel, device, new DateTime());
        }
        BigDecimal gsmLevel = getGSMPercentageLevel(reportCtx);
        if (gsmLevel != null) {
            measurementService.createGSMLevelMeasurement(gsmLevel, device, new DateTime());
        }
        return true;
    }

    private boolean isDeviceSituation(ReportContext reportCtx) {
        return RFV16Constants.MESSAGE_TYPE_V4.equalsIgnoreCase(reportCtx.getEntry(2))
                && RFV16Constants.COMMAND_DISPLAY_DEVICE_SITUATION.equalsIgnoreCase(reportCtx.getEntry(3));
    }
    
    private BigDecimal getBatteryPercentageLevel(ReportContext reportCtx) {
        return asPercentage(reportCtx.getEntryAsNumber(6), 0, 6);
    }

    private BigDecimal getGSMPercentageLevel(ReportContext reportCtx) {
        return asPercentage(reportCtx.getEntryAsNumber(4), 0, 31);
    }
    
    private static BigDecimal asPercentage(BigDecimal val, int min, int max) {
        if (val == null) {
            return null;
        }
        int result = ((val.intValue() - min) * 100) / (max - min);
        return new BigDecimal(result);
    }

}
