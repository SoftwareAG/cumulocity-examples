package com.cumulocity.greenbox.server;

import org.springframework.context.annotation.*;
import org.springframework.context.annotation.ComponentScan.Filter;

import com.cumulocity.greenbox.server.service.DefaultCredentialsResolver;

@Configuration
@ComponentScan(basePackages = "com.cumulocity.greenbox.server", excludeFilters = @Filter(type = FilterType.ANNOTATION, value = Configuration.class))
public class GreenBoxAgentFeature {

    @Bean
    public DefaultCredentialsResolver credentialsResolver() {
        return new DefaultCredentialsResolver();
    }
}
