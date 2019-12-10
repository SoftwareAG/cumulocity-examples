package com.cumulocity.agent.snmp.cucumber.tools;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskExecutor {

	public static boolean run(Task task) {
		return run(task, 60);
	}

	public static boolean run(Task task, int timeoutSeconds) {
		log.info("Starting task with " + timeoutSeconds + " seconds timeout");
		long timeout = System.currentTimeMillis() + (1000 * timeoutSeconds);
		boolean successful = false;
		while (!successful && timeout > System.currentTimeMillis()) {
			try {
				successful = task.run();
			} catch (Exception e) {
				log.info("Waiting for task to complete successfully within "
						+ ((timeout - System.currentTimeMillis()) / 1000) + "s");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// swallow
				}
			}
		}

		return successful;
	}
}
