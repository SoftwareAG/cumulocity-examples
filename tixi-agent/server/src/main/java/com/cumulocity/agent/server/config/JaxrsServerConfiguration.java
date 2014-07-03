package com.cumulocity.agent.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.cumulocity.agent.server.jaxrs.JaxrsServer;

@Configuration
@ComponentScan(basePackageClasses = JaxrsServer.class)
public class JaxrsServerConfiguration {

    
}
