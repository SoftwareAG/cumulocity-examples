package com.cumulocity.agent.snmp.platform.pubsub.queue;

import com.cumulocity.agent.snmp.configuration.SnmpAgentGatewayProperties;
import com.cumulocity.agent.snmp.persistence.AbstractQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;

@Repository
public class EventQueue extends AbstractQueue {
    private static final String EVENT_QUEUE_NAME = "EVENT";

    @Autowired
    public EventQueue(SnmpAgentGatewayProperties snmpAgentGatewayProperties) {
        super(EVENT_QUEUE_NAME,
                Paths.get(
                        System.getProperty("user.home"),
                        ".snmp",
                        snmpAgentGatewayProperties.getGatewayIdentifier().toLowerCase(),
                        "chronicle",
                        "queues",
                        EVENT_QUEUE_NAME.toLowerCase()).toFile()
        );
    }
}
