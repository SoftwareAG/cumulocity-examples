package c8y.trackeragent.protocol.queclink.parser;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.Position;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;

/**
 * Example crash report of Queclink device
 * +RESP:GTCRA,260301,135790246811220,,00,0,4.3,92,70.0,121.354335,31.222073,20090214
 *   013254,0460,0000,18d8,6141,00,20090214093254,11F0$
 *
 */
@Component
public class QueclinkCrashReport extends QueclinkParser {
    
    public static final String ONLINE_REP = "+RESP";
    public static final String BUFFER_REP = "+BUFF";
    public static final String CRASH_REPORT = "GTCRA";
    
    protected final TrackerAgent trackerAgent;
    
    @Autowired
    public QueclinkCrashReport(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
    }
    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        String[] reportType = reportCtx.getReport()[0].split(":");
        if (ONLINE_REP.equals(reportType[0]) || BUFFER_REP.equals(reportType[0])) {
            if (CRASH_REPORT.equals(reportType[1])) {
                return processCrashReport(reportCtx);
            }
        }
        return false;
    }
    private boolean processCrashReport(ReportContext reportCtx) {
        
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        
        int gpsReportStart = 5;
        int gpsReportEnd = 11;
        // create location update event
        Position pos = new Position();
        DateTime dateTime = getQueclinkDevice().getReportDateTime(reportCtx.getReport());
        //DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        //DateTime dateTime = formatter.parseDateTime(reportCtx.getEntry(reportCtx.getNumberOfEntries() - 2));

        if (reportCtx.getEntry(gpsReportStart + 3).length() > 0 
                && reportCtx.getEntry(gpsReportStart + 4).length() > 0
                && reportCtx.getEntry(gpsReportStart + 5).length() > 0) {
            pos.setAlt(new BigDecimal(reportCtx.getEntry(gpsReportStart + 3)));
            pos.setLng(new BigDecimal(reportCtx.getEntry(gpsReportStart + 4)));
            pos.setLat(new BigDecimal(reportCtx.getEntry(gpsReportStart + 5)));

            EventRepresentation locationEvent = buildLocationEvent(device, pos, dateTime);
            device.setPosition(locationEvent);
            device.crashDetectedEvent(pos, dateTime);
        } else {
            device.crashDetectedEvent(dateTime);
        }
        return true;
    }
    
    public EventRepresentation buildLocationEvent(TrackerDevice device, Position position, DateTime dateTime) {
        EventRepresentation locationUpdate = new EventRepresentation();
        locationUpdate.setType(TrackerDevice.LU_EVENT_TYPE);
        locationUpdate.setText("Crash detected");
        locationUpdate.setSource(asSource(device.getGId()));
        locationUpdate.setDateTime(dateTime);
        locationUpdate.set(position);

        return locationUpdate;
    }
    
    public ManagedObjectRepresentation asSource(GId gId) {
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(gId);
        return source;
    }
}
