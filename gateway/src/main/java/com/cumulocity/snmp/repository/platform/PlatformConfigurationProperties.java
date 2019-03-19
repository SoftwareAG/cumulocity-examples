package com.cumulocity.snmp.repository.platform;


import com.cumulocity.snmp.repository.platform.PlatformProperties.DeviceBootstrapConfigurationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlatformConfigurationProperties {

    @Value("${C8Y.baseURL:http://developers.cumulocity.com}")
    private String url;

    @Value("${C8Y.forceInitialHost:true}")
    private boolean forceInitialHost;

    @Value("${C8Y.bootstrap.tenant:management}")
    private String tenant;

    @Value("${C8Y.bootstrap.user:devicebootstrap}")
    private String user;

    @Value("${C8Y.bootstrap.password:Fhdt1bb1f}")
    private String password;

    @Bean
    public PlatformProperties platformProperties() {
        return PlatformProperties.builder()
                .url(url)
                .forceInitialHost(forceInitialHost)
                .bootstrap(DeviceBootstrapConfigurationProperties.builder()
                        .tenant(tenant.trim())
                        .user(user.trim())
                        .password(password.trim())
                        .build())
                .build();
    }

}
