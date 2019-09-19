package com.cumulocity.agent.snmp.platform.pubsub.subscriber;

import com.cumulocity.agent.snmp.platform.pubsub.service.EventPubSub;
import com.cumulocity.agent.snmp.platform.service.SnmpAgentGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventSubscriber extends Subscriber<EventPubSub> {

    @Autowired
    private SnmpAgentGatewayService snmpAgentGatewayService;

    @Override
    public void handleMessage(String message) {
        snmpAgentGatewayService.getEventApi().create(new EventRepresentation(message));
    }


    public static class EventRepresentation extends com.cumulocity.rest.representation.event.EventRepresentation {

        private String jsonString;

        public EventRepresentation() {
        }

        public EventRepresentation(String jsonString) {
            this.jsonString = jsonString;
        }

        @Override
        public String toJSON() {
            return jsonString;
        }
    }
}
