package com.cumulocity.agent.server.config;

import static java.lang.Boolean.getBoolean;
import static org.springframework.scheduling.support.TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER;

import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableAsync
@EnableScheduling
public class SchedulingConfiguration {

    public static final String PROP_POOL_SIZE = "scheduler.pool.size";

    public static final int DEFAULT_POOL_SIZE = 10;

    public static boolean isEnabled() {
        // add -Dno-async=true to disable scheduling and async execution
        return !getBoolean("no-async");
    }

    @Bean
    @Autowired
    public SchedulerFactoryBean scheduler(Environment environment) {
        return new SchedulerFactoryBean(environment);
    }

    @Bean
    @Autowired
    public Executor executor(SchedulerFactoryBean factory) {
        return factory.getAsyncExecutor();
    }

    public static class SchedulerFactoryBean implements FactoryBean<TaskScheduler>, AsyncConfigurer, SchedulingConfigurer {

        private final ThreadPoolTaskScheduler scheduler;

        public SchedulerFactoryBean(Environment environment) {
            scheduler = new ThreadPoolTaskScheduler();
            scheduler.setPoolSize(environment.getProperty(PROP_POOL_SIZE, Integer.class, DEFAULT_POOL_SIZE));
            scheduler.setErrorHandler(LOG_AND_SUPPRESS_ERROR_HANDLER);
            scheduler.setWaitForTasksToCompleteOnShutdown(true);
        }

        @PostConstruct
        public void initialize() {
            scheduler.afterPropertiesSet();
        }

        @PreDestroy
        public void shutdown() {
            scheduler.shutdown();
        }

        public ThreadPoolTaskScheduler getScheduler() {
            return scheduler;
        }

        @Override
        public TaskScheduler getObject() throws Exception {
            return getScheduler();
        }

        @Override
        public Class<?> getObjectType() {
            return TaskScheduler.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }

        @Override
        public Executor getAsyncExecutor() {
            return scheduler;
        }

        @Override
        public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
            taskRegistrar.setTaskScheduler(scheduler);
        }
    }
}
