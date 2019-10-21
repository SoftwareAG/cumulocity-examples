package com.cumulocity.agent.snmp.platform.pubsub.publisher;

import com.cumulocity.agent.snmp.platform.pubsub.service.PubSub;
import com.cumulocity.rest.representation.BaseResourceRepresentation;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class Publisher<PS extends PubSub<?>, R extends BaseResourceRepresentation> {

    @Autowired
    private PS pubSub;

    public void publish(R resource) {
        pubSub.publish(resource.toJSON());
    }
}
