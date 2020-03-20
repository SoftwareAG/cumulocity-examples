package com.cumulocity.agent.snmp.platform.pubsub.publisher;

import com.cumulocity.agent.snmp.platform.pubsub.service.EventPubSub;
import com.cumulocity.rest.representation.event.EventRepresentation;
import org.springframework.stereotype.Service;

@Service
public class EventPublisher extends Publisher<EventPubSub, EventRepresentation> {
}
