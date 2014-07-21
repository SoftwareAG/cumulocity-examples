package com.cumulocity.agent.server.config;

import static com.cumulocity.agent.server.context.Scopes.CONTEXT_SCOPE;
import static com.cumulocity.agent.server.context.Scopes.DEVICE_SCOPE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cumulocity.agent.server.context.ContextScopeContainerRegistry;
import com.cumulocity.agent.server.context.DeviceContextScope;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.context.DeviceScopeContainerRegistry;
import com.google.common.collect.ImmutableMap;

@Configuration
public class ScopesConfiguration {
    
    @Bean
    @Autowired
    public CustomScopeConfigurer contextScopeConfigurer(DeviceContextService contextService,
            ContextScopeContainerRegistry contextScopeContainerRegistry, DeviceScopeContainerRegistry deviceScopeContainerRegistry) {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.setScopes(ImmutableMap.<String, Object> builder()
                .put(CONTEXT_SCOPE, new DeviceContextScope(contextService, contextScopeContainerRegistry))
                .put(DEVICE_SCOPE, new DeviceContextScope(contextService, deviceScopeContainerRegistry)).build());
        return configurer;
    }
    
}
