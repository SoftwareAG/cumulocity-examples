package com.cumulocity.agent.snmp.platform.pubsub.service;

import com.cumulocity.agent.snmp.platform.pubsub.queue.AlarmQueue;
import org.springframework.stereotype.Service;

@Service
public class AlarmPubSub extends PubSub<AlarmQueue> {
}
