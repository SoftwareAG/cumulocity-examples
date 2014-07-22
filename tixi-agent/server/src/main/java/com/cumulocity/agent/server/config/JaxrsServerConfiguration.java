package com.cumulocity.agent.server.config;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.cumulocity.agent.server.context.*;
import com.cumulocity.agent.server.jaxrs.JaxrsServer;

@Configuration
@ComponentScan(basePackageClasses = JaxrsServer.class)
@Import({CommonConfiguration.class, ScopesConfiguration.class})
public class JaxrsServerConfiguration {

    @Bean
    public ContextFilter filter(DeviceContextService contextService,
            List<DeviceCredentailsResolver<HttpServletRequest>> deviceCredentailsResolvers,
            DeviceBootstrapDeviceCredentialsSupplier deviceBootstrapDeviceCredentialsSupplier) {
        return new ContextFilter(contextService, deviceCredentailsResolvers, deviceBootstrapDeviceCredentialsSupplier);
    }

    @Bean
    public AuthorizationHeaderDeviceCredentialsResolver authorizationHeaderDeviceCredentialsResolver() {
        return new AuthorizationHeaderDeviceCredentialsResolver();
    }
}
