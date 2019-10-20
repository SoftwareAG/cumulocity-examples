package com.cumulocity.agent.snmp.platform.pubsub.publisher;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.cumulocity.agent.snmp.platform.model.AlarmMapping;
import com.cumulocity.agent.snmp.platform.model.AlarmSeverity;
import com.cumulocity.agent.snmp.platform.pubsub.service.AlarmPubSub;
import com.cumulocity.model.event.CumulocitySeverities;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

@RunWith(MockitoJUnitRunner.class)
public class AlarmPublisherTest {

    @Mock
    private AlarmPubSub alarmPubSub;

    @InjectMocks
    private AlarmPublisher alarmPublisher;

    @Before
    public void setup() {
        DateTimeUtils.setCurrentMillisFixed(System.currentTimeMillis());
    }

    @After
    public void teardown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldPublishAlarm() {
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(1));

        AlarmRepresentation alarm = new AlarmRepresentation();
        alarm.setSource(source);
        alarm.setSeverity(CumulocitySeverities.CRITICAL.name());
        alarm.setText("NEW CRITICAL ALARM");
        alarm.setDateTime(DateTime.now());

        alarmPublisher.publish(alarm);

        Mockito.verify(alarmPubSub).publish(alarm.toJSON());
    }

    @Test
    public void shouldPublishAlarmFromAlarmMapping() {
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(1));

        AlarmMapping mapping = new AlarmMapping();
        mapping.setSeverity(AlarmSeverity.MAJOR.name());
        mapping.setType(AlarmMapping.c8y_TRAPReceivedFromUnknownDevice);
        mapping.setText("ALARM TEXT");

        alarmPublisher.publish(mapping, source);

        AlarmRepresentation alarm = new AlarmRepresentation();
        alarm.setSource(source);
        alarm.setSeverity(mapping.getSeverity());
        alarm.setDateTime(DateTime.now());
        alarm.setType(mapping.getType());
        alarm.setText(mapping.getText());

        Mockito.verify(alarmPubSub).publish(alarm.toJSON());
    }
}