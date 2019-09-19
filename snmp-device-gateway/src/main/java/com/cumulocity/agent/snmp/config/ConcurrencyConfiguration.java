package com.cumulocity.agent.snmp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ConcurrencyConfiguration {

	@Value("#{'${gateway.scheduler.threadpool.size:10}'.trim()}")
	private Integer schedulerPoolSize;

	@Value("#{'${gateway.executor.threadpool.coreSize:10}'.trim()}")
	private Integer executorCorePoolSize;

	@Value("#{'${gateway.executor.threadpool.maxSize:20}'.trim()}")
	private Integer executorMaxPoolSize;

	@Bean("taskScheduler")
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(schedulerPoolSize);
		threadPoolTaskScheduler.setThreadNamePrefix("scheduler-");
		return threadPoolTaskScheduler;
	}

	@Bean("taskExecutor")
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		return createExecutor(executorCorePoolSize, executorMaxPoolSize, "background-");
	}

	private ThreadPoolTaskExecutor createExecutor(int core, int max, String prefix) {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(core);
		executor.setMaxPoolSize(max);
		executor.setThreadNamePrefix(prefix);
		return executor;
	}

	public int getMeasurementThreadPoolSize() {
		return schedulerPoolSize * 30/100; // 10% of the total threads available
	}

	public int getAlarmThreadPoolSize() {
		return schedulerPoolSize * 10/100; // 10% of the total threads available
	}

	public int getEventThreadPoolSize() {
		return schedulerPoolSize * 10/100; // 10% of the total threads available
	}
}