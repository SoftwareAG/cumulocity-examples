/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent_it.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.utils.message.TrackerMessage;

public class SimulatorWorker implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SimulatorWorker.class);

    private final SimulatorContext context;

    public SimulatorWorker(SimulatorContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        SimulatorTask task;
        do {
            task = context.poll();
            processTask(task);
        } while (task != null);
    }

    private void processTask(SimulatorTask task) {
        if (task == null) {
            logger.info("No more tasks. Finish");
            return;
        }
        logger.info("Execute task: {}", task);
        try {
            TrackerMessage message = task.getMessage();
            task.getSocketWriter().write(message.asBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
