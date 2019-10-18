package com.cumulocity.agent.snmp.platform.pubsub.publisher;

import com.cumulocity.agent.snmp.platform.pubsub.service.PubSub;
import com.cumulocity.rest.representation.BaseResourceRepresentation;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class Publisher<PS extends PubSub<?>> {

    @Autowired
    private PS pubSub;

    protected void publish(BaseResourceRepresentation resource) {
        pubSub.publish(resource.toJSON());
    }
}
