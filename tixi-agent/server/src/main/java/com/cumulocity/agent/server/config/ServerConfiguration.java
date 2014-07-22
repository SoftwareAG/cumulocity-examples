package com.cumulocity.agent.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ 
		CommonConfiguration.class, 
		ContextConfiguration.class, 
		ScopesConfiguration.class, 
		CumulocityClientConfiguration.class,
        RepositoryConfiguration.class,
        SchedulingConfiguration.class})
public class ServerConfiguration {

}
