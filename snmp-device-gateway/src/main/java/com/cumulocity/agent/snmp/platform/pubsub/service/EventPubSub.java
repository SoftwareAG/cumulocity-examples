package com.cumulocity.agent.snmp.platform.pubsub.service;

import org.springframework.stereotype.Service;

import com.cumulocity.agent.snmp.platform.pubsub.queue.EventQueue;

@Service
public class EventPubSub extends PubSub<EventQueue> {
}

