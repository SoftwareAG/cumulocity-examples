package com.cumulocity.agent.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ CommonConfiguration.class, ContextConfiguration.class, CumulocityClientConfiguration.class })
public class ServerConfiguration {

}
