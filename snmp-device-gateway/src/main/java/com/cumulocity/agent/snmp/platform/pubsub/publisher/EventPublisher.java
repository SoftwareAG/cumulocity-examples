package com.cumulocity.agent.snmp.platform.pubsub.publisher;

import com.cumulocity.agent.snmp.platform.model.EventMapping;
import com.cumulocity.agent.snmp.platform.pubsub.service.EventPubSub;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
public class EventPublisher extends Publisher<EventPubSub> {

    public void publish(EventMapping eventMapping, ManagedObjectRepresentation source) {
        EventRepresentation newEvent = new EventRepresentation();
        newEvent.setSource(source);
        newEvent.setDateTime(DateTime.now());
        newEvent.setType(eventMapping.getType());
        newEvent.setText(eventMapping.getText());

        publish(newEvent);
    }
}
