package com.cumulocity.agent.snmp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties
@PropertySources(value = {
        @PropertySource(value = "file:${user.home}/.snmp/snmp-agent-gateway.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:/etc/snmp-agent-gateway/snmp-agent-gateway.properties", ignoreResourceNotFound = false)})
public class Main {
    public static void main(String... args) {
        SpringApplication.run(Main.class, args);
    }
}