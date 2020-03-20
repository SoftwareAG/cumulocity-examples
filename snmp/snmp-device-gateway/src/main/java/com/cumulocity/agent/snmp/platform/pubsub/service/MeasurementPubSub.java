package com.cumulocity.agent.snmp.platform.pubsub.service;

import com.cumulocity.agent.snmp.platform.pubsub.queue.MeasurementQueue;
import org.springframework.stereotype.Service;

@Service
public class MeasurementPubSub extends PubSub<MeasurementQueue> {
}
