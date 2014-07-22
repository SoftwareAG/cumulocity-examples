package com.cumulocity.agent.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.cumulocity.agent.server.repository")
public class RepositoryConfiguration {

}
