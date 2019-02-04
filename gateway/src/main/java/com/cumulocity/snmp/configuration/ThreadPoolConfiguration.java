package com.cumulocity.snmp.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfiguration {

    @Value("${gateway.threadPoolSize:16}")
    private int threadPoolSize;

    @Bean
    public Executor worker() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}
