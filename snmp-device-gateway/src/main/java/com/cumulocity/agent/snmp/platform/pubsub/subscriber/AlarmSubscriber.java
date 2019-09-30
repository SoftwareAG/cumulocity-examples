package com.cumulocity.agent.snmp.platform.pubsub.subscriber;

import com.cumulocity.agent.snmp.platform.pubsub.service.AlarmPubSub;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlarmSubscriber extends Subscriber<AlarmPubSub> {

    @Autowired
    private AlarmApi alarmApi;

    @Override
    public int getConcurrentSubscriptionsCount() {
        return concurrencyConfiguration.getSchedulerPoolSize() * 10/100; // 10% of the total threads available for scheduler
    }

    @Override
    public void handleMessage(String message) {
        alarmApi.create(new AlarmRepresentation(message));
    }

    public static class AlarmRepresentation extends com.cumulocity.rest.representation.alarm.AlarmRepresentation {

        private String jsonString;

        public AlarmRepresentation() {
        }

        AlarmRepresentation(String jsonString) {
            this.jsonString = jsonString;
        }

        @Override
        public String toJSON() {
            return jsonString;
        }
    }
}
