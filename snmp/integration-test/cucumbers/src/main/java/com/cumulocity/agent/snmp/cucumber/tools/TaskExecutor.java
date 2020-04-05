/*
 * Copyright © 2012 - 2017 Cumulocity GmbH.
 * Copyright © 2017 - 2020 Software AG, Darmstadt, Germany and/or its licensors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
