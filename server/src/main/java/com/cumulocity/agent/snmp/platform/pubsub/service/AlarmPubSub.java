package com.cumulocity.agent.snmp.platform.pubsub.service;

import com.cumulocity.agent.snmp.configuration.SnmpAgentGatewayProperties;
import com.cumulocity.agent.snmp.platform.pubsub.queue.AlarmQueue;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.AlarmSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlarmPubSub extends PubSub<AlarmQueue, AlarmSubscriber> {

    @Autowired
    private SnmpAgentGatewayProperties snmpAgentGatewayProperties;


    @Override
    public int getSubscriptionThreadCount() {
        return snmpAgentGatewayProperties.getAlarmThreadPoolSize();
    }
}
