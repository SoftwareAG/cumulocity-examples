package com.cumulocity.agent.snmp.pubsub.publisher;

import com.cumulocity.agent.snmp.pubsub.service.EventPubSub;
import com.cumulocity.rest.representation.event.EventRepresentation;
import org.springframework.stereotype.Service;

@Service
public class EventPublisher extends Publisher<EventPubSub, EventRepresentation> {
}
