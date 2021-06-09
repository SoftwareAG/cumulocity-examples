/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.cumulocity.agent.server.ServerBuilder;
import com.cumulocity.agent.server.feature.ContextFeature;
import com.cumulocity.agent.server.feature.RepositoryFeature;

/**
 * Migration for "rid of super user" task:
 * 
	1. Change credentials for tracker-agent MO.
		– Use standard bootstrap way using rest client and new device request id = tracker-agent-{tenant}.
	2. Store tenant credentials in device.properties file.
	3. Change owner of tenant tracker-agent.
	4. Change owner of all tenant tracker devices.
	5. Change entries in device.properties for tracker devices.
	
	Requirements:
	1. File /etc/tracker-agent-migration/tracker-agent-migration.properties
	2. File /etc/tracker-agent-migration/device.properties - device.properties file from target platform
	
	Output:
	1. File /etc/tracker-agent-migration/output/device.properties 
	2. Files /etc/tracker-agent-migration/output/device-{tenant}.properties - one per tenant 
 *
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(value = { "c8y.migration"})
public class MigrationApp {
	
    private static final Logger logger = LoggerFactory.getLogger(MigrationApp.class);
    
    public static void main(String[] args) {
        logger.info("tracker-agent-migration is starting.");
        serverBuilder().run(args);
    }

    public static ServerBuilder serverBuilder() {
        //@formatter:off
        return ServerBuilder.on(8689)
            .application("tracker-agent-migration")
            .logging("tracker-agent-server-migration-logging")
            .loadConfiguration("tracker-agent-migration")
            .enable(MigrationApp.class)
            .enable(ContextFeature.class)
            .enable(RepositoryFeature.class)
            .useWebEnvironment(false);
        //@formatter:on
    }
    
}
