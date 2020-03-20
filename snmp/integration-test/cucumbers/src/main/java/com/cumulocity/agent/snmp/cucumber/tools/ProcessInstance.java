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
