package com.cumulocity.agent.snmp.platform.pubsub.queue;

import com.cumulocity.agent.snmp.configuration.SnmpAgentGatewayProperties;
import com.cumulocity.agent.snmp.persistence.AbstractQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.nio.file.Paths;

@Repository
public class AlarmQueue extends AbstractQueue {
    private static final String ALARM_QUEUE_NAME = "ALARM";

    @Autowired
    public AlarmQueue(SnmpAgentGatewayProperties snmpAgentGatewayProperties) {
        super(ALARM_QUEUE_NAME,
                Paths.get(
                        System.getProperty("user.home"),
                        ".snmp",
                        snmpAgentGatewayProperties.getGatewayIdentifier().toLowerCase(),
                        "chronicle",
                        "queues",
                        ALARM_QUEUE_NAME.toLowerCase()).toFile()
        );
    }
}
