package com.cumulocity.agent.snmp.platform.pubsub.service;

import org.springframework.stereotype.Service;

import com.cumulocity.agent.snmp.platform.pubsub.queue.AlarmQueue;

@Service
public class AlarmPubSub extends PubSub<AlarmQueue> {
}
