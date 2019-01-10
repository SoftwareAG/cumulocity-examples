package com.cumulocity.snmp.repository;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.cumulocity.snmp.utils.PlatformRepositoryUtils.handleException;
import static com.cumulocity.snmp.utils.PlatformRepositoryUtils.handleSuccess;

@Slf4j
@Repository
public class DeviceCredentialsRepository {

    @Autowired
    private Platform bootstrapPlatform;

    public Optional<DeviceCredentialsRepresentation> get(String identifier) {
        try {
            final DeviceCredentialsApi deviceCredentialsApi = bootstrapPlatform.getDeviceCredentialsApi();
            return handleSuccess(deviceCredentialsApi.pollCredentials(identifier));
        } catch (final Exception ex) {
            return handleException(ex);
        }
    }

    @VisibleForTesting
    public void setBootstrapPlatform(Platform platform){
        bootstrapPlatform = platform;
    }
}
