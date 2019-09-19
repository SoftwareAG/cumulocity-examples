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
        @PropertySource(value = "file:${user.home}/.snmp/snmp-agent-gateway.properties", ignoreResourceNotFound = false)})
public class Main {
    public static void main(String... args) {
//        try {
            SpringApplication.run(Main.class, args);
//        } catch (UnsatisfiedDependencyException dependencyException) {
//            String path = System.getProperty("user.home");
////            if (dependencyException.contains(IllegalArgumentException.class) && dependencyException.contains(BeanCreationException.class)) {
//                log.error("=========================================================================================\n\n\n,", dependencyException);
//                log.error("Please place the snmp-agent-gateway.properties file to a proper location: E.g." + path + "/.snmp/snmp-agent-gateway.properties");
//                log.error("OR /etc/snmp-agent-gateway/snmp-agent-gateway.properties ");
//                log.error("OR classpath:META-INF/spring/snmp-agent-gateway.properties\n\n\n");
//                log.error("=========================================================================================");
//                System.exit(1);
////            }
//        }



    }
}