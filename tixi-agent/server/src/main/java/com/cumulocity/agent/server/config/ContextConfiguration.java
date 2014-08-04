package com.cumulocity.agent.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cumulocity.agent.server.context.*;

@Configuration
public class ContextConfiguration {

    @Bean
    public DeviceContextService contextService() {
        return new DeviceContextServiceImpl();
    }

    @Bean
    public ContextScopeContainerRegistry contextScopeContainerRegistry() {
        return new ContextScopeContainerRegistry();
    }

    @Bean
    public DeviceScopeContainerRegistry deviceScopeContainerRegistry() {
        return new DeviceScopeContainerRegistry();
    }

    @Bean
    public DeviceBootstrapDeviceCredentialsSupplier deviceBootstrapDeviceCredentialsSupplier() {
        return new DeviceBootstrapDeviceCredentialsSupplier();
    }

}
