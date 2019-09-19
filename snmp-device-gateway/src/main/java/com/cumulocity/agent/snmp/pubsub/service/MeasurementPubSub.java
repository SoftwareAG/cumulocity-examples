package com.cumulocity.agent.snmp.pubsub.service;

import com.cumulocity.agent.snmp.config.ConcurrencyConfiguration;
import com.cumulocity.agent.snmp.pubsub.queue.MeasurementQueue;
import com.cumulocity.agent.snmp.pubsub.subscriber.MeasurementSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeasurementPubSub extends PubSub<MeasurementQueue, MeasurementSubscriber> {

    @Autowired
    private ConcurrencyConfiguration concurrencyConfiguration;


    @Override
    public int getSubscriptionThreadCount() {
        return concurrencyConfiguration.getMeasurementThreadPoolSize();
    }
}
