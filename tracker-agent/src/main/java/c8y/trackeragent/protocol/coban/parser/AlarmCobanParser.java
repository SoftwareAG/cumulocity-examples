package c8y.trackeragent.protocol.coban.parser;

import static com.cumulocity.model.event.CumulocityAlarmStatuses.ACTIVE;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;

public class AlarmCobanParser extends CobanParser {
    
    private static Logger logger = LoggerFactory.getLogger(AlarmCobanParser.class);
    
    // @formatter:off
    public final static List<AlarmType> alarmTypes = Arrays.<AlarmType>asList(
        AlarmType.LOW_BATTERY
    );
    // @formatter:on
    
    public AlarmCobanParser(TrackerAgent trackerAgent) {
        super(trackerAgent);
    }

    @Override
    protected boolean accept(String[] report) {
        if (report.length < 2) {
            return false;
        }
        return getAlarmType(report) != null;
    }
    
    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        AlarmType alarmType = getAlarmType(reportCtx.getReport());
        logger.info("Process alarm {} for imei {}.", alarmType, reportCtx.getImei());
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        AlarmRepresentation alarm = newAlarm(device);
        alarmType.populateAlarm(alarm, reportCtx);
        logger.info("Create alarm {}.", alarm);
        device.createAlarm(alarm);
        return true;
    }

    private AlarmRepresentation newAlarm(TrackerDevice device) {
        AlarmRepresentation alarm = new AlarmRepresentation();
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(device.getGId());
        alarm.setSource(source);
        alarm.setTime(new Date());
        alarm.setStatus(ACTIVE.toString());
        return alarm;
    }

    @Override
    protected String doParse(String[] report) {
        return CobanServerMessages.extractImeiValue(report[0]);
    }
    
    public AlarmType getAlarmType(String[] report) {
        for (AlarmType alarmType : alarmTypes) {
            if (alarmType.accept(report)) {
                return alarmType;
            }
        }
        return null;
    }

}
