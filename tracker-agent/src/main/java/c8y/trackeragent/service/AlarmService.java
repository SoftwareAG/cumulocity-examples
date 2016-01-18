package c8y.trackeragent.service;

import static com.cumulocity.model.event.CumulocityAlarmStatuses.ACTIVE;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.coban.parser.CobanAlarmType;
import c8y.trackeragent.protocol.rfv16.parser.RFV16AlarmType;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class AlarmService {
    
    private static Logger logger = LoggerFactory.getLogger(AlarmService.class);
    
    public AlarmRepresentation createCobanAlarm(ReportContext reportCtx, CobanAlarmType alarmType, TrackerDevice device) {
        AlarmRepresentation alarm = newAlarm(device);
        alarmType.populateAlarm(alarm, reportCtx);
        logger.info("Create alarm {}.", alarm);
        device.createAlarm(alarm);
        return alarm;
    }
    
    public void clearCobanAlarm(ReportContext reportCtx, CobanAlarmType alarmType, TrackerDevice device) {
        AlarmRepresentation alarm = device.findActiveAlarm(alarmType.asC8yType());
        if (alarm != null) {
            logger.info("Clear alarm {}.", alarm);
            device.clearAlarm(alarm);
        }
    }
    
    public AlarmRepresentation createRFV16Alarm(ReportContext reportCtx, RFV16AlarmType alarmType, TrackerDevice device) {
        AlarmRepresentation alarm = newAlarm(device);
        alarmType.populateAlarm(alarm, reportCtx);
        logger.info("Create alarm {}.", alarm);
        device.createAlarm(alarm);
        return alarm;
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

    public void populateLocationEventByAlarm(EventRepresentation event, AlarmRepresentation alarm) {
        populateLocationEventByAlarms(event, Collections.singletonList(alarm));
    }

    public void populateLocationEventByAlarms(EventRepresentation event, Collection<AlarmRepresentation> alarms) {
        String text = Joiner.on("|").join(Iterables.transform(alarms, ALARM_TO_TEXT));
        event.setText(text);
    }
    
    private static Function<AlarmRepresentation, String> ALARM_TO_TEXT = new Function<AlarmRepresentation, String>() {

        @Override
        public String apply(@Nullable AlarmRepresentation input) {
            return input.getText();
        }
    };

}
