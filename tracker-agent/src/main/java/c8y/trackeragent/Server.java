/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package c8y.trackeragent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.sdk.client.Platform;

public class Server implements Runnable {

	public static final String PORT_PROP = "port";
	public static final String DEFAULT_PORT = "8081";

	public Server(Platform platform, Properties props) throws IOException {
		this.platform = platform;
		int port = Integer.parseInt(props.getProperty(PORT_PROP, DEFAULT_PORT));
		this.server = new ServerSocket(port);
	}

	@Override
	public void run() {
		while (true) {
			try {
				logger.debug("Waiting for connection");
				Socket client = server.accept();
				logger.debug(
						"Accepted connection from {}, launching worker thread.",
						client.getRemoteSocketAddress());
				TrackerManager trackerManager = new TrackerManager(platform);
				
				QueclinkTrackerReader reader = new QueclinkTrackerReader(client, trackerManager);
				new Thread(reader).start();
				
				QueclinkTrackerCommands writer = new QueclinkTrackerCommands(client, trackerManager);
			} catch (IOException e) {
				logger.warn("Couldn't accept connection", e);
			}
		}
	}

	private Logger logger = LoggerFactory.getLogger(Agent.class);
	private Platform platform;
	private ServerSocket server;
}
