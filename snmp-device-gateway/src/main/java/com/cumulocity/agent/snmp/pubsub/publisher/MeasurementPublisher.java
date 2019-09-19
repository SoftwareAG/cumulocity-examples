package com.cumulocity.agent.snmp.pubsub.publisher;

import com.cumulocity.agent.snmp.pubsub.service.MeasurementPubSub;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import org.springframework.stereotype.Service;

@Service
public class MeasurementPublisher extends Publisher<MeasurementPubSub, MeasurementRepresentation> {
}
