package com.cumulocity.agent.snmp.platform.pubsub.queue;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.persistence.AbstractQueue;

@Repository
public class MeasurementQueue extends AbstractQueue {
    private static final String MEASUREMENT_QUEUE_NAME = "MEASUREMENT";

    @Autowired
    public MeasurementQueue(GatewayProperties gatewayProperties) {
        super(MEASUREMENT_QUEUE_NAME,
                Paths.get(
                        System.getProperty("user.home"),
                        ".snmp",
                        gatewayProperties.getGatewayIdentifier().toLowerCase(),
                        "chronicle",
                        "queues",
                        MEASUREMENT_QUEUE_NAME.toLowerCase()).toFile()
        );
    }
}
