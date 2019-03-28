package com.cumulocity.snmp.utils.gateway;

import com.cumulocity.snmp.configuration.service.GatewayConfigurationProperties;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class Scheduler {

    @Autowired
    TaskScheduler taskScheduler;

    @Autowired
    GatewayConfigurationProperties properties;

    public void scheduleWithFixedDelay(final Runnable task) {
        if (properties.getBootstrapFixedDelay() != null && properties.getBootstrapFixedDelay() > 0) {
            taskScheduler.scheduleWithFixedDelay(task, properties.getBootstrapFixedDelay());
        }
    }

    public void scheduleWithFixedDelay(final Runnable task, long delay) {
        taskScheduler.scheduleWithFixedDelay(task, delay);
    }

    public void scheduleOnce(Runnable task1) {
        if (properties.getBootstrapFixedDelay() != null && properties.getBootstrapFixedDelay() > 0) {
            taskScheduler.schedule(task1, DateTime.now().plusMillis(properties.getBootstrapFixedDelay()).toDate());
        }
    }
}
