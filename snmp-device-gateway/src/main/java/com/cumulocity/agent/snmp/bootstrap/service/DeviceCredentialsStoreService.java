package com.cumulocity.agent.snmp.bootstrap.service;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cumulocity.agent.snmp.bootstrap.model.DeviceCredentialsKey;
import com.cumulocity.agent.snmp.bootstrap.repository.DeviceCredentialsStore;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.model.JSONBase;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class DeviceCredentialsStoreService {

    private final GatewayProperties gatewayProperties;

    private final DeviceCredentialsStore deviceCredentialsStore;


    void store(DeviceCredentialsRepresentation credentials) {
        if(credentials == null) {
            throw new NullPointerException("credentials");
        }

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
    void closeDeviceCredentialsStore() {
        deviceCredentialsStore.close();
    }

    private DeviceCredentialsKey createDeviceCredentialsKey() {
        return new DeviceCredentialsKey(
                gatewayProperties.getBaseUrl(),
                gatewayProperties.getBootstrapProperties().getTenantId(),
                gatewayProperties.getBootstrapProperties().getUsername());
    }
}
