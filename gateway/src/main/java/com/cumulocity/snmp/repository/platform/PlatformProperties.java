package com.cumulocity.snmp.repository.platform;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Builder
public class PlatformProperties {

    private final String url;
    private final boolean forceInitialHost;
    private final DeviceBootstrapConfigurationProperties bootstrap;

    @Value
    @Builder
    public static class DeviceBootstrapConfigurationProperties {
        private final String tenant;
        private final String user;
        private final String password;
    }

}
