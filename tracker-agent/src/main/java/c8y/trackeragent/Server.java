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
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.protocol.mapping.TrackerFactory;
import c8y.trackeragent.utils.TrackerConfiguration;

/**
 * The server listens to connections from devices and starts threads for
 * handling device communication for particular types of devices. (Currently,
 * only GL200, Telic, Coban)
 */
public class Server implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    
    private ServerSocket serverSocket;
    private final TrackerConfiguration config;
    private final ExecutorService reportsExecutor;
    private final ExecutorService requestsExecutor;
    private final TrackerFactory trackerFactory;
    private final int localPort;
    private volatile boolean running = true;

    Server(
            TrackerConfiguration config, 
            TrackerFactory trackerFactory,
            ExecutorService reportsExecutor,
            ExecutorService requestsExecutor,
            int localPort) {
        this.config = config;
        this.trackerFactory = trackerFactory;
        this.reportsExecutor = reportsExecutor;
        this.requestsExecutor = requestsExecutor;
        this.localPort = localPort;
    }
    
    public void init() {
        logger.info("Initialize server for port {}", localPort);
        try {
            this.serverSocket = new ServerSocket(localPort);
        } catch (IOException e) {
            throw new RuntimeException("Cant init agent tracker server!", e);
        }
    }
    
    public void destroy() {
        running = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.warn("Problem occured trying close server socket: " + e.getMessage());
            }
        }
    }

    @Override
    public void run() {
        logger.info("Start server for port {}", localPort);
        running = true;
        while (running) {
            accept();
        }
    }

    private void accept() {
        try {
            logger.debug("Waiting for connection on port {}", serverSocket.getLocalPort());
            if (serverSocket.isClosed()) {
                return;
            }
            Socket client = serverSocket.accept();
            client.setSoTimeout(config.getClientTimeout());
            logger.debug("Accepted connection from {}, launching worker thread.", client.getRemoteSocketAddress());
            requestsExecutor.execute(new RequestHandler(reportsExecutor, client, trackerFactory));
        } catch (Exception e) {
            logger.error("Error while processing:", e);
        }
    }
}
