package com.cumulocity.agent.snmp.platform.pubsub.publisher;

import com.cumulocity.agent.snmp.platform.pubsub.service.EventPubSub;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.joda.time.DateTime;
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

    @Test
    public void shouldPublishAlarm() {
        EventRepresentation event = new EventRepresentation();
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(1));

        event.setSource(source);
        event.setText("NEW CRITICAL EVENT");
        event.setDateTime(DateTime.now());

        eventPublisher.publish(event);

        Mockito.verify(eventPubSub).publish(event.toJSON());
    }
}