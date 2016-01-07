package c8y.trackeragent;

import java.math.BigDecimal;

import c8y.Position;

import com.cumulocity.sdk.client.SDKException;

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
public class TelicLocationReport implements Parser {
    
    public static final int ALTITUDE = 12;
    public static final int LONGITUDE = 4;
    public static final int LATITUDE = 5;
    public static final BigDecimal DIVISOR = new BigDecimal(10000);

    private TrackerAgent trackerAgent;

    public TelicLocationReport(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
    }

    @Override
    public String parse(String[] report) throws SDKException {
        return report[0].substring(4, 10);   
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        Position pos = new Position();
        String[] report = reportCtx.getReport();
        pos.setAlt(new BigDecimal(report[ALTITUDE]));
        pos.setLng(new BigDecimal(report[LONGITUDE]).divide(DIVISOR));
        pos.setLat(new BigDecimal(report[LATITUDE]).divide(DIVISOR));
        device.setPosition(pos);
        return true;
    }

}
