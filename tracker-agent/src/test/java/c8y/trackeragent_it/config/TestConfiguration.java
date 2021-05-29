/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent_it.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import c8y.trackeragent_it.TestSettings;

@Configuration
@EnableAutoConfiguration
@PropertySource(value = { 
        "file:/etc/tracker-agent/test.properties" })
public class TestConfiguration {
    
    @Value("${C8Y.tenant}")
    private String tenant;

    @Value("${C8Y.username}")
    private String username;

    @Value("${C8Y.password}")
    private String password;
    
    @Value("${C8Y.host}")
    private String host;

    @Value("${tracker-agent.host}")
    private String trackerAgentHost;
    
    @Value("${C8Y.devicebootstrap.user}")
    private String bootstrapUser;
    
    @Value("${C8Y.devicebootstrap.password}")
    private String bootstrapPassword;
    
    @Bean
    public TestSettings testSettings() {
        //@formatter:off
        TestSettings result = new TestSettings()
            .setC8yTenant(tenant)
            .setC8yUser(username)
            .setC8yPassword(password)
            .setC8yHost(host)
            .setTrackerAgentHost(trackerAgentHost)
            .setBootstrapUser(bootstrapUser)
            .setBootstrapPassword(bootstrapPassword);
        System.out.println(result);
        return result;
        //@formatter:on            
    }
}
