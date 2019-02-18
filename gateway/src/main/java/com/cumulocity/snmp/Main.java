package com.cumulocity.snmp;

import com.cumulocity.snmp.annotation.core.PersistableType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.context.annotation.AdviceMode.ASPECTJ;

@Slf4j
@ComponentScan(value = "com.cumulocity.snmp", includeFilters = {@ComponentScan.Filter(classes = {PersistableType.class})})
@SpringBootApplication
@EnableScheduling
@EnableAsync(mode = ASPECTJ)
@EnableConfigurationProperties
@PropertySources(value = {
        @PropertySource(value = "file:${user.home}/snmp/snmp-agent-gateway.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${snmp.conf.dir:/etc}/snmp/snmp-agent-gateway.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:META-INF/spring/snmp-agent-gateway.properties", ignoreResourceNotFound = true)})
public class Main {
    public static void main(String... args) {
        SpringApplication.run(Main.class, args);
    }
}
