package com.cumulocity.agent.snmp.platform.pubsub.publisher;

import com.cumulocity.agent.snmp.platform.model.EventMapping;
import com.cumulocity.agent.snmp.platform.pubsub.service.EventPubSub;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
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

@RunWith(MockitoJUnitRunner.class)
public class EventPublisherTest {

    @Mock
    private EventPubSub eventPubSub;

    @InjectMocks
    private EventPublisher eventPublisher;

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

        EventRepresentation event = new EventRepresentation();
        event.setSource(source);
        event.setText("NEW CRITICAL EVENT");
        event.setDateTime(DateTime.now());

        eventPublisher.publish(event);

        Mockito.verify(eventPubSub).publish(event.toJSON());
    }

    @Test
    public void shouldPublishEventFromEventMapping() {
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(1));

        EventMapping mapping = new EventMapping();
        mapping.setType("c8y_TRAPReceivedFromUnknownDevice");
        mapping.setText("EVENT TEXT");

        eventPublisher.publish(mapping, source);

        EventRepresentation event = new EventRepresentation();
        event.setSource(source);
        event.setDateTime(DateTime.now());
        event.setType(mapping.getType());
        event.setText(mapping.getText());

        Mockito.verify(eventPubSub).publish(event.toJSON());
    }
}