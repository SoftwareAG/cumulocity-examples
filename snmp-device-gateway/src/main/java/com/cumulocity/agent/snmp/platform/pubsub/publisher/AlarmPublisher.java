package com.cumulocity.agent.snmp.platform.pubsub.publisher;

import com.cumulocity.agent.snmp.platform.model.AlarmMapping;
import com.cumulocity.agent.snmp.platform.pubsub.service.AlarmPubSub;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
public class AlarmPublisher extends Publisher<AlarmPubSub> {

    public void publish(AlarmMapping alarmMapping, ManagedObjectRepresentation source) {
        AlarmRepresentation newAlarm = new AlarmRepresentation();
        newAlarm.setSource(source);
        newAlarm.setDateTime(DateTime.now());
        newAlarm.setType(alarmMapping.getType());
        newAlarm.setText(alarmMapping.getText());
        newAlarm.setSeverity(alarmMapping.getSeverity());

        publish(newAlarm);
    }
}
