package com.cumulocity.agent.snmp.platform.pubsub.publisher;

import com.cumulocity.agent.snmp.platform.pubsub.service.AlarmPubSub;
import com.cumulocity.model.event.CumulocitySeverities;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AlarmPublisherTest {

    @Mock
    private AlarmPubSub alarmPubSub;

    @InjectMocks
    private AlarmPublisher alarmPublisher;

    @Test
    public void shouldPublishAlarm() {
        AlarmRepresentation alarm = new AlarmRepresentation();
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(1));

        alarm.setSource(source);
        alarm.setSeverity(CumulocitySeverities.CRITICAL.name());
        alarm.setText("NEW CRITICAL ALARM");
        alarm.setDateTime(DateTime.now());

        alarmPublisher.publish(alarm);

        Mockito.verify(alarmPubSub).publish(alarm.toJSON());
    }
}