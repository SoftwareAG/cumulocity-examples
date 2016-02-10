package c8y.trackeragent.protocol.telic.parser;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.Position;
import c8y.trackeragent.Parser;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.protocol.telic.TelicConstants;

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
    
    private static final int LOG_CODE = 0;
    private static final int LOG_TIMESTAMP = 1;
    private static final int GPS_TIMESTAMP = 3;
    public static final int LONGITUDE = 4;
    public static final int LATITUDE = 5;
    public static final int FIX_TYPE = 6;
    public static final int ALTITUDE = 12;
    
    public static final BigDecimal LAT_AND_LNG_DIVISOR = new BigDecimal(10000);

    private TrackerAgent trackerAgent;

    @Autowired
    public TelicLocationReport(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
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
        position.setAlt(getAltitude(reportCtx));
        position.setLng(getLongitue(reportCtx));
        position.setLat(getLatitude(reportCtx));
        LogCodeType logCodeType = getLogCodeType(reportCtx);
        if (logCodeType != null) {
            position.setProperty(TelicConstants.LOG_CODE_TYPE, logCodeType.getLabel());
        }
        position.setProperty(TelicConstants.LOG_TIMESTAMP, getLogTimestamp(reportCtx));
        position.setProperty(TelicConstants.GPS_TIMESTAMP, getGPSTimestamp(reportCtx));
        FixType fixType = getFixType(reportCtx);
        if (fixType != null) {
            position.setProperty(TelicConstants.FIX_TYPE, fixType.getLabel());
        }
        
        device.setPosition(locationUpdateEvent, position);
        return true;
    }
    
    private LogCodeType getLogCodeType(ReportContext reportCtx) {
        String codeStr = reportCtx.getEntry(LOG_CODE).substring(10);
        if ("99".equals(codeStr)) {
            return LogCodeType.TIME_EVENT;
        } else if ("98".equals(codeStr)) {
            return LogCodeType.DISTANCE_EVENT;
        }
        //TODO
        logger.warn("Cant establish event code for value {} in report {}", codeStr, reportCtx);
        return null;
    }
    
    private Date getLogTimestamp(ReportContext reportCtx) {
        String eventTypeStr = reportCtx.getEntry(LOG_TIMESTAMP);
        return TelicConstants.TIMESTAMP_FORMATTER.parseDateTime(eventTypeStr).toDate();
    }
    
    private Date getGPSTimestamp(ReportContext reportCtx) {
        String eventTypeStr = reportCtx.getEntry(GPS_TIMESTAMP);
        return TelicConstants.TIMESTAMP_FORMATTER.parseDateTime(eventTypeStr).toDate();
    }
    
    private BigDecimal getLongitue(ReportContext reportCtx) {
        return getCoord(reportCtx, LONGITUDE);
    }
    
    private BigDecimal getLatitude(ReportContext reportCtx) {
        return getCoord(reportCtx, LATITUDE);
    }
    
    private BigDecimal getAltitude(ReportContext reportCtx) {
        return new BigDecimal(reportCtx.getEntry(ALTITUDE));
    }
    
    private BigDecimal getCoord(ReportContext reportCtx, int index) {
        BigDecimal incomingValue = new BigDecimal(reportCtx.getEntry(index));
        return incomingValue.divide(LAT_AND_LNG_DIVISOR);        
    }

    private FixType getFixType(ReportContext reportCtx) {
        String fixTypeStr = reportCtx.getEntry(FIX_TYPE);
        FixType fixType = FixType.forValue(fixTypeStr);
        if(fixType == null) {
            logger.warn("Cant establish fix type for value {} in report {}", fixTypeStr, reportCtx);
        }
        return fixType;
    }
}
