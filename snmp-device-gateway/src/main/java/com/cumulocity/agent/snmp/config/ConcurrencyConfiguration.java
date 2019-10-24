package com.cumulocity.agent.snmp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ConcurrencyConfiguration {

	@Autowired
	private GatewayProperties gatewayProperties;

	@Bean("taskScheduler")
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		int poolSize = gatewayProperties.getThreadPoolSizeForScheduledTasks();

		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(poolSize);
		threadPoolTaskScheduler.setThreadNamePrefix("scheduler-");

		return threadPoolTaskScheduler;
	}
}