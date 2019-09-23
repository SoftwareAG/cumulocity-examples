package com.cumulocity.agent.snmp.platform.pubsub.service;

import com.cumulocity.agent.snmp.config.ConcurrencyConfiguration;
import com.cumulocity.agent.snmp.platform.pubsub.queue.AlarmQueue;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.AlarmSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlarmPubSub extends PubSub<AlarmQueue, AlarmSubscriber> {

    @Autowired
    private ConcurrencyConfiguration concurrencyConfiguration;


    @Override
    public int getSubscriptionThreadCount() {
        return concurrencyConfiguration.getAlarmThreadPoolSize();
    }
}
