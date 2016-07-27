package c8y.trackeragent.protocol.telic.parser;

import static com.cumulocity.model.DateTimeConverter.date2String;
import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.Position;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.CommonConstants;
import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.protocol.telic.TelicConstants;
import c8y.trackeragent.service.MeasurementService;
import c8y.trackeragent.tracker.Parser;

/**
 * <p>
 * Location report of the Telic tracker.
 * </p>
 * 
 * <pre>
 * 072118718299,200311121210,0,200311121210,115864,480332,3,4,67,4,,,599,11032,,010 1,00,238,0,0,0
 * </pre>
 * 
 */
@Component
public class TelicLocationReport implements Parser, TelicFragment {

    private static Logger logger = LoggerFactory.getLogger(TelicLocationReport.class);

    static final int LOG_CODE = 0;

    static final int LOG_TIMESTAMP = 1;

    static final int GPS_TIMESTAMP = 3;

    static final int LONGITUDE = 4;

    static final int LATITUDE = 5;

    public static final int FIX_TYPE = 6;

    static final int SPEED = 7;

    static final int SATELLITES_FOR_CALCULATION = 9;

    static final int ALTITUDE = 12;
    
    static final int MILEAGE = 13;
    
    static final int DIGITAL_INPUT = 15;
    
    static final int BATTERY = 17;
    
    static final int DEVICE_ID = 20;
    

    public static final BigDecimal MILEAGE_DIVISOR = new BigDecimal(1000);
    public static final BigDecimal BATTERY_MULTIPLIER = new BigDecimal(0.00345);
    public static final BigDecimal BATTERY_INCREMENTOR = new BigDecimal(3.4);
    public static final MathContext BATTERY_CALCULATION_MODE = new MathContext(3, RoundingMode.HALF_DOWN);
    public static final BigDecimal BATTERY_DIVISOR = new BigDecimal(1000);
    
    public static final List<String> devicesRequireBatteryCalculation = asList("0108", "0109");

    private TrackerAgent trackerAgent;

    private MeasurementService measurementService;

    @Autowired
    public TelicLocationReport(TrackerAgent trackerAgent, MeasurementService measurementService) {
        this.trackerAgent = trackerAgent;
        this.measurementService = measurementService;
    }

    @Override
    public String parse(String[] report) throws SDKException {
        return report[0].substring(4, 10);
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        logger.info("Parse position for telic tracker");
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        EventRepresentation locationUpdateEvent = device.aLocationUpdateEvent();
        Position position = new Position();
        DateTime dateTime = getLogTimestamp(reportCtx);
        if (dateTime == null) {
            dateTime = new DateTime();
        } else {
            position.setProperty(TelicConstants.LOG_TIMESTAMP, date2String(dateTime));
        }
        DateTime gpsTimestamp = getGPSTimestamp(reportCtx);
        if (gpsTimestamp != null) {
            position.setProperty(TelicConstants.GPS_TIMESTAMP, date2String(gpsTimestamp));
        }
        
        position.setLat(getLatitude(reportCtx));
        position.setLng(getLongitue(reportCtx));
        BigDecimal altitude = getAltitude(reportCtx);
        if (altitude != null) {
            position.setAlt(altitude);
            measurementService.createAltitudeMeasurement(altitude, device, dateTime);
        }
        LogCodeType logCodeType = getLogCodeType(reportCtx);
        if (logCodeType != null) {
            position.setProperty(CommonConstants.REPORT_REASON, logCodeType.getLabel());
            handleLogCodeType(device, logCodeType, dateTime);
        }
        locationUpdateEvent.setTime(dateTime.toDate());
        position.setProperty(CommonConstants.TRACKING_PROTOCOL, TrackingProtocol.TELIC);
        String fixType = getFixType(reportCtx);
        if (fixType != null) {
            position.setProperty(TelicConstants.FIX_TYPE, fixType);
        }
        Integer satellitesForCalculation = getSatellitesForCalculation(reportCtx);
        if (satellitesForCalculation != null) {
            position.setProperty(TelicConstants.SATELLITES, satellitesForCalculation);
        }

        device.setPosition(locationUpdateEvent, position);

        BigDecimal speed = getSpeed(reportCtx);
        if (speed != null) {
            measurementService.createSpeedMeasurement(speed, device, dateTime);
        }
        
        BigDecimal mileage = getMileage(reportCtx);
        if (mileage != null) {
            measurementService.createMileageMeasurement(mileage, device, dateTime);
        }
        String digitalInput = getDigitalInput(reportCtx);
        if(digitalInput != null) {
            handleDigitalInput(reportCtx, device, digitalInput, dateTime);
        }
        BigDecimal batteryLevel = getBatteryLevel(reportCtx);
        if (batteryLevel != null) {
            measurementService.createBatteryLevelMeasurement(batteryLevel, device, dateTime, "V");
        }
        return true;
    }

    private LogCodeType getLogCodeType(ReportContext reportCtx) {
        String codeStr = reportCtx.getEntry(LOG_CODE).substring(10);
        for (LogCodeType logCodeType : LogCodeType.values()) {
            if (logCodeType.match(codeStr)) {
                return logCodeType;
            }
        }
        logger.warn("Cant establish event code for value {} in report {}", codeStr, reportCtx);
        return null;
    }

    private DateTime getLogTimestamp(ReportContext reportCtx) {
        return getTimestamp(reportCtx, LOG_TIMESTAMP);
    }

    private DateTime getGPSTimestamp(ReportContext reportCtx) {
        return getTimestamp(reportCtx, GPS_TIMESTAMP);
    }

    private DateTime getTimestamp(ReportContext reportCtx, int index) {
        String timestampStr = reportCtx.getEntry(index);
        if(timestampStr == null) {
            return null;
        }
        return TelicConstants.TIMESTAMP_FORMATTER.parseDateTime(timestampStr);
    }

    private BigDecimal getLongitue(ReportContext reportCtx) {
        String value = reportCtx.getEntry(LONGITUDE);
        return PositionParser.LONGITUDE_PARSER.parse(value);
    }

    private BigDecimal getLatitude(ReportContext reportCtx) {
        String value = reportCtx.getEntry(LATITUDE);
        return PositionParser.LATITUDE_PARSER.parse(value);
    }

    private BigDecimal getAltitude(ReportContext reportCtx) {
        return new BigDecimal(reportCtx.getEntry(ALTITUDE));
    }

    private String getFixType(ReportContext reportCtx) {
        String fixTypeStr = reportCtx.getEntry(FIX_TYPE);
        FixType fixType = FixType.forValue(fixTypeStr);
        if (fixType == null) {
        	return fixTypeStr;
        }
        return fixType.getLabel();
    }

    private BigDecimal getSpeed(ReportContext reportCtx) {
        return reportCtx.getEntryAsNumber(SPEED);
    }

    private Integer getSatellitesForCalculation(ReportContext reportCtx) {
        return reportCtx.getEntryAsInt(SATELLITES_FOR_CALCULATION);
    }
    
    private BigDecimal getMileage(ReportContext reportCtx) {
        BigDecimal mileage = reportCtx.getEntryAsNumber(MILEAGE);
        return mileage == null ? null : mileage.divide(MILEAGE_DIVISOR);
    }
    
    BigDecimal getBatteryLevel(ReportContext reportCtx) {
        BigDecimal batteryLevel = reportCtx.getEntryAsNumber(BATTERY);
        if (batteryLevel == null) {
            return null;
        }
        
        if (isRequiredCalculation(reportCtx)) {
            return normalizeBatteryLevel(batteryLevel);
        } else {
            return convertToVolt(batteryLevel);
        }
    }

	private boolean isRequiredCalculation(ReportContext reportCtx) {
        String deviceId = reportCtx.getEntry(DEVICE_ID);
        if (deviceId == null) {
            return false;
        }
        return devicesRequireBatteryCalculation.contains(deviceId);
    }

    private BigDecimal convertToVolt(BigDecimal batteryLevel) {
        return batteryLevel.divide(BATTERY_DIVISOR);
    }

    public static BigDecimal normalizeBatteryLevel(BigDecimal batteryLevel) {
		return batteryLevel.multiply(BATTERY_MULTIPLIER, BATTERY_CALCULATION_MODE).add(BATTERY_INCREMENTOR, BATTERY_CALCULATION_MODE);
	}
    
    private String getDigitalInput(ReportContext reportCtx) {
        return reportCtx.getEntry(DIGITAL_INPUT);
    }
    
    private void handleLogCodeType(TrackerDevice device, LogCodeType logCodeType, DateTime dateTime) {
        switch (logCodeType) {
        case GEOFENCE_ENTER:
            device.geofenceEnter(dateTime);
            break;
        case GEOFENCE_EXIT:
            device.geofenceExit(dateTime);
            break;
        case MOTION_SENSOR_MOTION:
            device.motionEvent(true, dateTime);
            measurementService.createMotionMeasurement(true, device, dateTime);
            break;
        case MOTION_SENSOR_STATIONARY:
            device.motionEvent(false, dateTime);
            measurementService.createMotionMeasurement(false, device, dateTime);
            break;
        default:
            //do nothing
        }
    }
    
    private void handleDigitalInput(ReportContext reportCtx, TrackerDevice device, String digitalInput, DateTime dateTime) {
        if (digitalInput.length() < 2) {
            logger.warn("Digital input has unexpected size {} (expected more than 1)");
        }
        boolean chargerConnected = digitalInput.charAt(1) == '1';
        boolean chargerConnectedEventSent = reportCtx.isConnectionFlagOn("chargerConnectedEventSent");
        if (chargerConnected && !chargerConnectedEventSent) {
            device.chargerConnected(dateTime);
            reportCtx.setConnectionParam("chargerConnectedEventSent", Boolean.TRUE);
        } 
        if (!chargerConnected) {
            //reset connection state for next time when charger will be connected
            reportCtx.setConnectionParam("chargerConnectedEventSent", Boolean.FALSE);
        }
    }

}
