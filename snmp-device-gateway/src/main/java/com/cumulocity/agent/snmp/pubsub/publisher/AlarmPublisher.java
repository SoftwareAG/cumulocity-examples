package com.cumulocity.agent.snmp.pubsub.publisher;

import com.cumulocity.agent.snmp.pubsub.service.AlarmPubSub;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import org.springframework.stereotype.Service;

@Service
public class AlarmPublisher extends Publisher<AlarmPubSub, AlarmRepresentation> {
}
