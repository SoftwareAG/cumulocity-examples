package com.cumulocity.agent.snmp.platform.pubsub.subscriber;

import com.cumulocity.agent.snmp.platform.pubsub.service.AlarmPubSub;
import com.cumulocity.agent.snmp.platform.service.SnmpAgentGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlarmSubscriber extends Subscriber<AlarmPubSub> {

    @Autowired
    private SnmpAgentGatewayService snmpAgentGatewayService;

    @Override
    public void handleMessage(String message) {
        snmpAgentGatewayService.getAlarmApi().create(new AlarmRepresentation(message));
    }


    public static class AlarmRepresentation extends com.cumulocity.rest.representation.alarm.AlarmRepresentation {

        private String jsonString;

        public AlarmRepresentation() {
        }

        public AlarmRepresentation(String jsonString) {
            this.jsonString = jsonString;
        }

        @Override
        public String toJSON() {
            return jsonString;
        }
    }
}
