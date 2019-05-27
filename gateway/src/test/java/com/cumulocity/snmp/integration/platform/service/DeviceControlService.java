package com.cumulocity.snmp.integration.platform.service;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.google.common.base.Optional;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

@Component
public class DeviceControlService {

    private Map<String, DeviceCredentialsRepresentation> credentials = new HashMap<>();

    public void registerGateway(String device) {
        putCredentials(device, "tenant", "username", "passwrod");
    }

    public Optional<DeviceCredentialsRepresentation> pollCredentials(String deviceId) {
        if (credentials.containsKey(deviceId)) {
            return of(credentials.get(deviceId));
        } else {
            return absent();
        }
    }

    public DeviceCredentialsRepresentation putCredentials(final String deviceId, final String tenant, final String username, final String password) {
        final DeviceCredentialsRepresentation result = deviceCredentialsRepresentation(deviceId, tenant, username, password);
        credentials.put(deviceId, result);
        return result;
    }

    private DeviceCredentialsRepresentation deviceCredentialsRepresentation(final String deviceId, final String tenant, final String username, final String password) {
        DeviceCredentialsRepresentation representation = new DeviceCredentialsRepresentation();
        representation.setId(deviceId);
        representation.setTenantId(tenant);
        representation.setUsername(username);
        representation.setPassword(password);
        return representation;
    }
}
