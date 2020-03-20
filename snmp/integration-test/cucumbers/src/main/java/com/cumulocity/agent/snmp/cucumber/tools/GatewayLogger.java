package com.cumulocity.agent.snmp.cucumber.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
public class GatewayLogger {
	private final int BUFFER_SIZE = 500;
	private ConcurrentLinkedQueue<String> buffer = new ConcurrentLinkedQueue<>();

	private void addLine(String line) {
		if (buffer.size() == BUFFER_SIZE) {
			buffer.remove();
		}
		buffer.add(line);
	}

	public void attachToProcess(final Process process) {
		new Thread(() -> {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while (process.isAlive()) {
					line = reader.readLine();
					if (line != null) {
						log.info("GATEWAY LOG: " + line);
						addLine(line);
					} else {
						log.info("Gateway process finished - finishing logger process");
					}
				}
			} catch (IOException e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// swallow
				}
				if (process.isAlive()) {
					log.error(e.getMessage(), e);
				} else {
					log.info("Gateway process finished - finishing logger process");
				}
			}
		}).start();
	}

	private boolean matches(String regex) {
		for (String s : buffer) {
			if (s.matches(regex)) {
				return true;
			}
		}
		return false;
	}

	public boolean waitForLog(String regex, long timeout) throws InterruptedException {
		boolean found = false;
		timeout = timeout + System.currentTimeMillis();
		while (!found && System.currentTimeMillis() < timeout) {
			Thread.sleep(500);
			found = matches(regex);
		}
		return found;
	}
}