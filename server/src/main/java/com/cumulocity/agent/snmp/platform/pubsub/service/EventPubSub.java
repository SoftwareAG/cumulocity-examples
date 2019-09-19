package com.cumulocity.agent.snmp.platform.pubsub.service;

import com.cumulocity.agent.snmp.configuration.SnmpAgentGatewayProperties;
import com.cumulocity.agent.snmp.platform.pubsub.queue.EventQueue;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.EventSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventPubSub extends PubSub<EventQueue, EventSubscriber> {

    @Autowired
    private SnmpAgentGatewayProperties snmpAgentGatewayProperties;


    @Override
    public int getSubscriptionThreadCount() {
        return snmpAgentGatewayProperties.getEventThreadPoolSize();
    }
}

