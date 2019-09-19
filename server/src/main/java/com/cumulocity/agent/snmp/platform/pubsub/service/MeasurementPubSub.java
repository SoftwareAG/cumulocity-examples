package com.cumulocity.agent.snmp.platform.pubsub.service;

import com.cumulocity.agent.snmp.configuration.SnmpAgentGatewayProperties;
import com.cumulocity.agent.snmp.platform.pubsub.queue.MeasurementQueue;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.MeasurementSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeasurementPubSub extends PubSub<MeasurementQueue, MeasurementSubscriber> {

    @Autowired
    private SnmpAgentGatewayProperties snmpAgentGatewayProperties;


    @Override
    public int getSubscriptionThreadCount() {
        return snmpAgentGatewayProperties.getMeasurementThreadPoolSize();
    }
}
