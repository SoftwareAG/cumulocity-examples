package com.cumulocity.agent.snmp.platform.pubsub.subscriber;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.pubsub.service.EventPubSub;
import com.cumulocity.sdk.client.event.EventApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventSubscriber extends Subscriber<EventPubSub> {

    @Autowired
    private GatewayProperties.SnmpProperties snmpProperties;

    @Autowired
    private EventApi eventApi;

    @Override
    public int getConcurrentSubscriptionsCount() {
        // 10% of the total threads available for scheduler
        int count = snmpProperties.getTrapListenerThreadPoolSize() * 10 / 100;

        return (count <= 0)? 1 : count;
    }

    @Override
    public void handleMessage(String message) {
        eventApi.create(new EventRepresentation(message));
    }


    public static class EventRepresentation extends com.cumulocity.rest.representation.event.EventRepresentation {

        private String jsonString;

        public EventRepresentation() {
        }

        EventRepresentation(String jsonString) {
            this.jsonString = jsonString;
        }

        @Override
        public String toJSON() {
            return jsonString;
        }
    }
}
