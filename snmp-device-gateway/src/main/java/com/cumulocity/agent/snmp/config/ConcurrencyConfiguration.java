package com.cumulocity.agent.snmp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ConcurrencyConfiguration {

	@Value("#{'${gateway.scheduler.threadpool.size:10}'.trim()}")
	private Integer schedulerPoolSize;

	public int getSchedulerPoolSize() {
		return schedulerPoolSize;
	}

	@Bean("taskScheduler")
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(schedulerPoolSize);
		threadPoolTaskScheduler.setThreadNamePrefix("scheduler-");
		return threadPoolTaskScheduler;
	}
}