package c8y.trackeragent.protocol.telic.parser;

import java.math.BigDecimal;

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
    
    public static final int ALTITUDE = 12;
    public static final int LONGITUDE = 4;
    public static final int LATITUDE = 5;
    public static final BigDecimal DIVISOR = new BigDecimal(10000);

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
        String[] report = reportCtx.getReport();
        position.setAlt(new BigDecimal(report[ALTITUDE]));
        position.setLng(new BigDecimal(report[LONGITUDE]).divide(DIVISOR));
        position.setLat(new BigDecimal(report[LATITUDE]).divide(DIVISOR));
        LogCodeType logCodeType = getLogCodeType(reportCtx);
        if (logCodeType != null) {
            position.setProperty(TelicConstants.LOG_CODE_TYPE, logCodeType.getLabel());
        }
        device.setPosition(locationUpdateEvent, position);
        return true;
    }
    
    private LogCodeType getLogCodeType(ReportContext reportCtx) {
        String codeStr = reportCtx.getEntry(0).substring(10);
        if ("99".equals(codeStr)) {
            return LogCodeType.TIME_EVENT;
        } else if ("98".equals(codeStr)) {
            return LogCodeType.DISTANCE_EVENT;
        }
        //TODO
        logger.warn("Cant establish event code for value {} in report {}", codeStr, reportCtx);
        return null;
    }

}
