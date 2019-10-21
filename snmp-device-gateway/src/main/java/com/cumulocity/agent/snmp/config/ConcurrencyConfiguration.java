package com.cumulocity.agent.snmp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ConcurrencyConfiguration {

	@Autowired
	private GatewayProperties.SnmpProperties snmpProperties;

	@Bean("taskScheduler")
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(snmpProperties.getTrapListenerThreadPoolSize());
		threadPoolTaskScheduler.setThreadNamePrefix("scheduler-");
		return threadPoolTaskScheduler;
	}
}