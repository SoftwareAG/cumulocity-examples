package com.cumulocity.mibparser;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@MicroserviceApplication
@EnableAutoConfiguration
@PropertySources(value = {
        @PropertySource(value = "file:${user.home}/.mibparser/mibparser.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:/etc/mibparser/mibparser.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:META-INF/spring/mibparser.properties", ignoreResourceNotFound = true)
})
public class MibParserApplication {
    public static void main(String[] args) {
        SpringApplication.run(MibParserApplication.class, args);
    }
}