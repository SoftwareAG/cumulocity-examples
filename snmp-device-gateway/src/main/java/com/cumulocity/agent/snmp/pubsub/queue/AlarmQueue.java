package com.cumulocity.agent.snmp.pubsub.queue;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.persistence.AbstractQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;

@Repository
public class AlarmQueue extends AbstractQueue {
    private static final String ALARM_QUEUE_NAME = "ALARM";

    @Autowired
    public AlarmQueue(GatewayProperties gatewayProperties) {
        super(ALARM_QUEUE_NAME,
                Paths.get(
                        System.getProperty("user.home"),
                        ".snmp",
                        gatewayProperties.getGatewayIdentifier().toLowerCase(),
                        "chronicle",
                        "queues",
                        ALARM_QUEUE_NAME.toLowerCase()).toFile()
        );
    }
}
