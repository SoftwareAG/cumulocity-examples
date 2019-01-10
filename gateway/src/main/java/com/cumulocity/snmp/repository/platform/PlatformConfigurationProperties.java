package com.cumulocity.snmp.repository.platform;


import com.cumulocity.snmp.repository.platform.PlatformProperties.DeviceBootstrapConfigurationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlatformConfigurationProperties {

    @Value("${platform.url:http://developers.cumulocity.com}")
    private String url;

    @Value("${platform.forceInitialHost:true}")
    private boolean forceInitialHost;

    @Value("${platform.bootstrap.tenant:management}")
    private String tenant;

    @Value("${platform.bootstrap.user:devicebootstrap}")
    private String user;

    @Value("${platform.bootstrap.password:Fhdt1bb1f}")
    private String password;

    @Bean
    public PlatformProperties platformProperties() {
        return PlatformProperties.builder()
                .url(url)
                .forceInitialHost(forceInitialHost)
                .bootstrap(DeviceBootstrapConfigurationProperties.builder()
                        .tenant(tenant)
                        .user(user)
                        .password(password)
                        .build())
                .build();
    }

}
