package com.cumulocity.agent.snmp.service;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.repository.DeviceCredenialsStore;
import com.cumulocity.model.JSONBase;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
@Slf4j
public class DeviceCredentialsStoreService {

    @Autowired
    private GatewayProperties gatewayProperties;

    @Autowired
    private DeviceCredenialsStore deviceCredentialsStore;


    public void store(DeviceCredentialsRepresentation credentials) {
        deviceCredentialsStore.put(createDeviceCredentialsKey(), credentials.toJSON());
    }

    public DeviceCredentialsRepresentation fetch() {
        String deviceCredentialsJson = deviceCredentialsStore.get(createDeviceCredentialsKey());
        if(deviceCredentialsJson != null) {
            return JSONBase.fromJSON(deviceCredentialsJson, DeviceCredentialsRepresentation.class);
        }

        return null;
    }

    public DeviceCredentialsRepresentation remove() {
        String deviceCredentialsJson = deviceCredentialsStore.remove(createDeviceCredentialsKey());

        if(deviceCredentialsJson != null) {
            return JSONBase.fromJSON(deviceCredentialsJson, DeviceCredentialsRepresentation.class);
        }

        return null;
    }

    @PreDestroy
    private void closeDeviceCredentialsStore() {
        try {
            deviceCredentialsStore.close();
        } catch (Exception e) {
            log.error("Error while closing the '" + deviceCredentialsStore.getName() + "' Map.", e);
        }
    }

    private DeviceCredenialsStore.DeviceCredentialsKey createDeviceCredentialsKey() {
        return new DeviceCredenialsStore.DeviceCredentialsKey(
                gatewayProperties.getBaseUrl(),
                gatewayProperties.getBootstrapProperties().getTenantId(),
                gatewayProperties.getBootstrapProperties().getUsername());
    }
}
