package com.cumulocity.agent.snmp.bootstrap.service;

import com.cumulocity.agent.snmp.bootstrap.model.DeviceCredentialsKey;
import com.cumulocity.agent.snmp.bootstrap.repository.DeviceCredentialsStore;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.model.JSONBase;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class DeviceCredentialsStoreService {

    @Autowired
    private final GatewayProperties gatewayProperties;

    @Autowired
    private final DeviceCredentialsStore deviceCredentialsStore;


    void store(DeviceCredentialsRepresentation credentials) {
        deviceCredentialsStore.put(createDeviceCredentialsKey(), credentials.toJSON());
    }

    DeviceCredentialsRepresentation fetch() {
        String deviceCredentialsJson = deviceCredentialsStore.get(createDeviceCredentialsKey());
        if(deviceCredentialsJson != null) {
            return JSONBase.fromJSON(deviceCredentialsJson, DeviceCredentialsRepresentation.class);
        }

        return null;
    }

    DeviceCredentialsRepresentation remove() {
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
            log.error("Error while closing the '{}' Map.", deviceCredentialsStore.getName(), e);
        }
    }

    private DeviceCredentialsKey createDeviceCredentialsKey() {
        return new DeviceCredentialsKey(
                gatewayProperties.getBaseUrl(),
                gatewayProperties.getBootstrapProperties().getTenantId(),
                gatewayProperties.getBootstrapProperties().getUsername());
    }
}
