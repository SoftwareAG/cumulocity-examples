package com.cumulocity.agent.server.config;

import static com.cumulocity.agent.server.context.DeviceContextScope.CONTEXT_SCOPE;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cumulocity.agent.server.context.DeviceContextScope;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.context.DeviceContextServiceImpl;
import com.google.common.collect.ImmutableMap;

@Configuration
public class ContextConfiguration {

    @Bean
    public DeviceContextService contextService() {
        return new DeviceContextServiceImpl();
    }

    @Bean
    public DeviceContextScope contextScope() {
        return new DeviceContextScope(contextService());
    }

    @Bean
    public CustomScopeConfigurer contextScopeConfigurer() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.setScopes(ImmutableMap.<String, Object> builder()
                .put(CONTEXT_SCOPE, contextScope())
                .build());
        return configurer;
    }

}
