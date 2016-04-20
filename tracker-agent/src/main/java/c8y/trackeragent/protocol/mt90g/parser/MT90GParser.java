package c8y.trackeragent.protocol.mt90g.parser;

import static c8y.trackeragent.utils.LocationEventBuilder.aLocationEvent;
import static com.cumulocity.model.DateConverter.date2String;
import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
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

@Component
public class MT90GParser implements MT90GFragment, Parser {
    
    private static final Logger logger = LoggerFactory.getLogger(MT90GParser.class);
    
    private final TrackerAgent trackerAgent;
    private final MeasurementService measurementService;
    
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
        
        Integer satellitesForCalculation = reportCtx.getEntryAsInt(8);
        if (satellitesForCalculation != null) {
            position.setProperty(TelicConstants.SATELLITES, satellitesForCalculation);
        }
        DateTime gpsTimestamp = getTimestamp(reportCtx,6);
        if (gpsTimestamp != null) {
            position.setProperty(TelicConstants.GPS_TIMESTAMP, date2String(gpsTimestamp.toDate()));
        }
        Integer accuracy = reportCtx.getEntryAsInt(12);
        if (accuracy != null) {
            position.setAccuracy(accuracy);
        }
        device.setPosition(locationEvent, position);
        
        createMeasurements(reportCtx, device);
        return true;
    }

    private void createMeasurements(ReportContext reportCtx, TrackerDevice device) {
        BigDecimal speedValue = parseToBigDecimalOrNull(reportCtx.getEntry(10));
        if (speedValue != null) {
            measurementService.createSpeedMeasurement(speedValue, device);
        }
        BigDecimal gsmLevel = reportCtx.getEntryAsNumber(9);
        if (gsmLevel != null) {
            measurementService.createGSMLevelMeasurement(gsmLevel, device, new DateTime());
        }
        BigDecimal mileage = reportCtx.getEntryAsNumber(14);
        if (mileage != null) {
            BigDecimal mileageInKM = convertToKm(mileage, reportCtx);
            measurementService.createMileageMeasurement(mileageInKM, device, new DateTime());
        }
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
            return mileage.divide(new BigDecimal(1000), RoundingMode.HALF_DOWN);
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
            return Double.parseDouble(value);
        } catch (Exception e) {
            logger.info("Cannot parse to double value: " + value);
        }
        return 0;
    }
    
    private BigDecimal parseToBigDecimalOrNull(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            logger.info("Cannot parse to BigDecimal value: " + value);
        }
        return null;
    }

}
