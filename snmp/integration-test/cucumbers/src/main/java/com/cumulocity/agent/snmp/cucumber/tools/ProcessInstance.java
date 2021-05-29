/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
 * and/or its subsidiaries and/or its affiliates and/or their licensors. 
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

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessInstance {

    private Process process = null;

    public void start(ProcessBuilder processBuilder) throws IOException {
        processBuilder.redirectErrorStream(true);
        process = processBuilder.start();
    }

    public void stop() throws InterruptedException {
        if (process == null || !process.isAlive()) {
            log.info("Process is already stopped");
            return;
        }
        process.destroy();
        long timeout = System.currentTimeMillis() + 5000;
        while (process.isAlive() && System.currentTimeMillis() < timeout) {
            Thread.sleep(500);
        }
        if (process.isAlive()) {
            log.info("Graceful process shutdown failed. Killing process...");
            process.destroyForcibly();
            if (process.isAlive()) {
                throw new RuntimeException("Couldn't kill process!");
            }
        } else {
            log.info("Process finished");
        }
    }

    public Process get() {
        return process;
    }
}
