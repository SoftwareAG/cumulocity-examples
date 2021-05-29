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