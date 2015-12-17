package c8y.trackeragent.protocol.coban.service;

import static com.cumulocity.model.event.CumulocityAlarmStatuses.ACTIVE;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.coban.parser.AlarmType;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

public class AlarmService {
    
    private static Logger logger = LoggerFactory.getLogger(AlarmService.class);
    
    public void createAlarm(ReportContext reportCtx, AlarmType alarmType, TrackerDevice device) {
        AlarmRepresentation alarm = newAlarm(device);
        alarmType.populateAlarm(alarm, reportCtx);
        logger.info("Create alarm {}.", alarm);
        device.createAlarm(alarm);
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


}
