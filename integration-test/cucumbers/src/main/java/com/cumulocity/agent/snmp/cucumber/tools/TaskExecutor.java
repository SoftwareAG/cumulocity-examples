package com.cumulocity.agent.snmp.cucumber.tools;

import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskExecutor {

    public static boolean run(Callable<Boolean> task) {
        return run(task, 60);
    }

    public static boolean run(Callable<Boolean> task, int timeoutSeconds) {
        log.info("Starting task with " + timeoutSeconds + " seconds timeout");
        long timeout = System.currentTimeMillis() + (1000 * timeoutSeconds);
        boolean successful = false;
        while (!successful && timeout > System.currentTimeMillis()) {
            try {
                successful = task.call();
                if (!successful) {
                    log.info("Waiting for task to complete successfully within " + ((timeout - System.currentTimeMillis()) / 1000) + "s");
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                log.error("Exception occured in runnable task", e);
                throw new RuntimeException("Task executor runnable task failed");
            }
        }

        return successful;
    }
}
