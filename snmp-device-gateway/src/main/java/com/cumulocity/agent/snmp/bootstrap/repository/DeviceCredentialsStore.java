package com.cumulocity.agent.snmp.bootstrap.repository;

import com.cumulocity.agent.snmp.bootstrap.model.DeviceCredentialsKey;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.persistence.AbstractMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;

@Repository
public class DeviceCredentialsStore extends AbstractMap<DeviceCredentialsKey, String> {

    private static final String DEVICE_CREDENTIALS_STORE = "device-credentials-store";

    @Autowired
    DeviceCredentialsStore(GatewayProperties gatewayProperties) {
        super(DEVICE_CREDENTIALS_STORE,
                DeviceCredentialsKey.class,
                100,
                String.class,
                10_000,
                10, // Chronicle Map is optimized for 10 entries as we actually store only one entry with device credentials
                Paths.get(
                        System.getProperty("user.home"),
                        ".snmp",
                        gatewayProperties.getGatewayIdentifier().toLowerCase(),
                        "chronicle",
                        "maps",
                        DEVICE_CREDENTIALS_STORE.toLowerCase() + ".dat").toFile()
        );
    }
}
