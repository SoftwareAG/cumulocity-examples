package com.cumulocity.agent.snmp.platform.pubsub.service;

import org.springframework.stereotype.Service;

import com.cumulocity.agent.snmp.platform.pubsub.queue.MeasurementQueue;

@Service
public class MeasurementPubSub extends PubSub<MeasurementQueue> {
}
