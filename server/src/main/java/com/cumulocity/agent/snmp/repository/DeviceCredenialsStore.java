package com.cumulocity.agent.snmp.repository;

import com.cumulocity.agent.snmp.configuration.SnmpAgentGatewayProperties;
import com.cumulocity.agent.snmp.persistence.AbstractMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.nio.file.Paths;

@Repository
public class DeviceCredenialsStore extends AbstractMap<DeviceCredenialsStore.DeviceCredentialsKey, String> {

    public static final String DEVICE_CREDENIALS_STORE = "device-credenials-store";

    @Autowired
    public DeviceCredenialsStore(SnmpAgentGatewayProperties snmpAgentGatewayProperties) {
        super(DEVICE_CREDENIALS_STORE,
                DeviceCredenialsStore.DeviceCredentialsKey.class,
                100,
                String.class,
                10_000,
                10,
                Paths.get(
                        System.getProperty("user.home"),
                        ".snmp",
                        snmpAgentGatewayProperties.getGatewayIdentifier().toLowerCase(),
                        "chronicle",
                        "maps",
                        DEVICE_CREDENIALS_STORE.toLowerCase() + ".dat").toFile()
        );
    }


    @RequiredArgsConstructor
    @Getter
    public static final class DeviceCredentialsKey implements Serializable {
        private final String bootstrapUrl;

        private final String bootstrapTenant;

        private final String bootstrapUser;
    }
}
