/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import com.cumulocity.agent.server.ServerBuilder;

public class ExternalTomcatStarter extends SpringBootServletInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(ExternalTomcatStarter.class);
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
        logger.info("Configure tracker agent context for external container.");
        ServerBuilder serverBuilder = Main.serverBuilder();
        serverBuilder.configure(applicationBuilder);
        return applicationBuilder;
    }
}
