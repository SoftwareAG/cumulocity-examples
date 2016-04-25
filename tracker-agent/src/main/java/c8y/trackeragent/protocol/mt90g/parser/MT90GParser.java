package c8y.trackeragent.protocol.mt90g.parser;

import static c8y.trackeragent.utils.LocationEventBuilder.aLocationEvent;
import static com.cumulocity.model.DateConverter.date2String;
import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.Position;
import c8y.trackeragent.Parser;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.mt90g.MT90GConstants;
import c8y.trackeragent.protocol.telic.TelicConstants;
import c8y.trackeragent.service.MeasurementService;
import c8y.trackeragent.utils.LocationEventBuilder;

/**
 * example command: $$_157,358884051405608,AAA,35,35.713820,139.770668,160421082033,
 * V,0,4,0,111,0.0,627,121490,245809,440|10|0085|0439C053,0000,00D2|0000|0000|0ACB|0002,00000001,*8B\r\n
 *
 */
@Component
public class MT90GParser implements MT90GFragment, Parser {
    
    private static final BigDecimal KM_DIVISOR = new BigDecimal(1000);

    private static final Logger logger = LoggerFactory.getLogger(MT90GParser.class);
    
    private final TrackerAgent trackerAgent;
    private final MeasurementService measurementService;
    
    public static final MathContext BATTERY_CALCULATION_MODE = new MathContext(3, RoundingMode.HALF_DOWN);
    
    @Autowired
    public MT90GParser(TrackerAgent trackerAgent, MeasurementService measurementService) {
        this.trackerAgent = trackerAgent;
        this.measurementService = measurementService;
    }

    @Override
    public String parse(String[] report) throws SDKException {
        if (report.length > 1) {
            String imei = report[1];
            logger.debug("Imei = '{}'", imei);
            return imei;
        } else {
            return null;
        }
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        double lat = parseOrDefault(reportCtx.getEntry(4));
        double lng = parseOrDefault(reportCtx.getEntry(5));
        double alt = parseOrDefault(reportCtx.getEntry(13));
        
        LocationEventBuilder locationEventBuilder = aLocationEvent()
                .withSourceId(device.getGId())
                .withLat(valueOf(lat))
                .withLng(valueOf(lng))
                .withAlt(valueOf(alt));
        EventRepresentation locationEvent = locationEventBuilder.build();
        Position position = locationEvent.get(Position.class);
        
        Double satellitesForCalculation = parseOrNull(reportCtx.getEntry(8));
        if (satellitesForCalculation != null) {
            position.setProperty(TelicConstants.SATELLITES, satellitesForCalculation);
        }
        DateTime gpsTimestamp = getTimestamp(reportCtx,6);
        if (gpsTimestamp != null) {
            position.setProperty(TelicConstants.GPS_TIMESTAMP, date2String(gpsTimestamp.toDate()));
        }
        Double accuracy = parseOrNull(reportCtx.getEntry(12));
        if (accuracy != null) {
            position.setAccuracy(accuracy.longValue());
        }
        Double direction = parseOrNull(reportCtx.getEntry(11));
        if (direction != null) {
            position.setProperty(MT90GConstants.DIRECTION, direction);
        }
        device.setPosition(locationEvent, position);
        
        createMeasurements(reportCtx, device);
        return true;
    }

    private void createMeasurements(ReportContext reportCtx, TrackerDevice device) {
        DateTime date = new DateTime();

        BigDecimal speedValue = parseToBigDecimalOrNull(reportCtx.getEntry(10));
        if (speedValue != null) {
            measurementService.createSpeedMeasurement(speedValue, device);
        }
        BigDecimal gsmLevel = parseToBigDecimalOrNull(reportCtx.getEntry(9));
        if (gsmLevel != null) {
            measurementService.createGSMLevelMeasurement(gsmLevel, device, date);
        }
        BigDecimal mileage = parseToBigDecimalOrNull(reportCtx.getEntry(14));
        if (mileage != null) {
            BigDecimal mileageInKM = convertToKm(mileage, reportCtx);
            measurementService.createMileageMeasurement(mileageInKM, device, date);
        }
        BigDecimal batteryVoltage = getBattery(reportCtx.getEntry(18));
        if (batteryVoltage != null) {
            measurementService.createBatteryLevelMeasurement(batteryVoltage, device, date, "V");
        }
    }
    
    private BigDecimal getBattery(String analogEntry) {
        if (!StringUtils.isEmpty(analogEntry)) {
            return null;
        }
        String[] entries = analogEntry.split("|");
        if (entries.length > 4) {
            Integer ad4Int = getBatteryAnalogEntry(entries[3]);
            return calculateBatteryVoltage(ad4Int);
        }
        return null;
    }

    private BigDecimal calculateBatteryVoltage(Integer ad4Int) {
        if (ad4Int == null) {
            return null;
        }
        BigDecimal ad4 = new BigDecimal(ad4Int);
        BigDecimal result = ad4.multiply(new BigDecimal(3.3), BATTERY_CALCULATION_MODE)
                                    .multiply(new BigDecimal(2), BATTERY_CALCULATION_MODE);
        return result.divide(new BigDecimal(4096), BATTERY_CALCULATION_MODE);
    }

    private Integer getBatteryAnalogEntry(String hexAd4) {
        try {
            return Integer.parseInt(hexAd4, 16);
        } catch (NumberFormatException e) {
            logger.warn("Failed to parse tracker battery ad4 value to decimal: {}", hexAd4);
        }
        return null;
    }

    private DateTime getTimestamp(ReportContext reportCtx, int index) {
        String timestampStr = reportCtx.getEntry(index);
        if(timestampStr == null) {
            return null;
        }
        try {
            return MT90GConstants.TIMESTAMP_FORMATTER.parseDateTime(timestampStr);
        } catch (Exception e) {
            logger.warn("Failed to parse tracker {} gps time: {}", reportCtx.getImei(), timestampStr);
        }
        return null;
    }

    private BigDecimal convertToKm(BigDecimal mileage, ReportContext reportCtx) {
        try {
            return mileage.divide(KM_DIVISOR, RoundingMode.HALF_DOWN);
        } catch (Exception e) {
            logger.warn("Failed to convert tracker {} mileage {} to KM", reportCtx.getImei(), mileage);
        }
        return BigDecimal.ZERO;
    }

    private double parseOrDefault(String value) {
        if (StringUtils.isEmpty(value)) {
            return 0;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (Exception e) {
            logger.info("Cannot parse to double value: " + value);
        }
        return 0;
    }
    
    private Double parseOrNull(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            return Double.valueOf(value.trim());
        } catch (Exception e) {
            logger.info("Cannot parse to double value: " + value);
        }
        return null;
    }
    
    private BigDecimal parseToBigDecimalOrNull(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (Exception e) {
            logger.info("Cannot parse to BigDecimal value: " + value);
        }
        return null;
    }

}
