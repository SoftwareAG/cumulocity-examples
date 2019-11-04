package com.cumulocity.agent.snmp.platform.pubsub.service;

import com.cumulocity.agent.snmp.platform.pubsub.queue.EventQueue;
import org.springframework.stereotype.Service;

@Service
public class EventPubSub extends PubSub<EventQueue> {
}

