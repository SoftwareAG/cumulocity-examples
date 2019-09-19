package com.cumulocity.agent.snmp.platform.pubsub.queue;

import com.cumulocity.agent.snmp.configuration.SnmpAgentGatewayProperties;
import com.cumulocity.agent.snmp.persistence.AbstractQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;

@Repository
public class MeasurementQueue extends AbstractQueue {
    private static final String MEASUREMENT_QUEUE_NAME = "MEASUREMENT";

    @Autowired
    public MeasurementQueue(SnmpAgentGatewayProperties snmpAgentGatewayProperties) {
        super(MEASUREMENT_QUEUE_NAME,
                Paths.get(
                        System.getProperty("user.home"),
                        ".snmp",
                        snmpAgentGatewayProperties.getGatewayIdentifier().toLowerCase(),
                        "chronicle",
                        "queues",
                        MEASUREMENT_QUEUE_NAME.toLowerCase()).toFile()
        );
    }
}
