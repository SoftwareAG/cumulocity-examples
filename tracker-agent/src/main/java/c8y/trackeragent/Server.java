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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.devicebootstrap.DeviceBootstrapProcessor;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.devicebootstrap.DeviceBinder;
import c8y.trackeragent.logger.TracelogAppenders;
import c8y.trackeragent.operations.OperationDispatchers;
import c8y.trackeragent.utils.ConfigUtils;
import c8y.trackeragent.utils.TrackerConfiguration;
import c8y.trackeragent.utils.TrackerContext;

/**
 * The server listens to connections from devices and starts threads for
 * handling device communication for particular types of devices. (Currently,
 * only GL200 and Telic.)
 */
public class Server implements Runnable {

    private static final int REPORTS_EXECUTOR_POOL_SIZE = 10;
    private static final int REQUESTS_EXECUTOR_POOL_SIZE = 10;

    private static final Logger logger = LoggerFactory.getLogger(Server.class);


    private ServerSocket serverSocket;
    private final TrackerContext trackerContext;
    private final TrackerAgent trackerAgent;
    private final ExecutorService reportsExecutor;
    private final ExecutorService requestsExecutor;
    private final DeviceBootstrapProcessor deviceBootstrapProcessor;
    private final DeviceBinder deviceBinder;
    private volatile boolean running = true;

    public Server(TrackerConfiguration commonConfiguration) {
        this.trackerContext = new TrackerContext(commonConfiguration);
        this.trackerAgent = new TrackerAgent(trackerContext);
        this.reportsExecutor = Executors.newFixedThreadPool(REPORTS_EXECUTOR_POOL_SIZE);
        this.requestsExecutor = Executors.newFixedThreadPool(REQUESTS_EXECUTOR_POOL_SIZE);
        OperationDispatchers operationDispatchers = new OperationDispatchers(trackerContext, trackerAgent);
        TracelogAppenders tracelogAppenders = new TracelogAppenders(trackerContext);
        this.deviceBootstrapProcessor = new DeviceBootstrapProcessor(trackerAgent);
        this.deviceBinder = new DeviceBinder(
                operationDispatchers, tracelogAppenders, DeviceCredentialsRepository.get());
    }
    
    public Server() {
        this(ConfigUtils.get().loadCommonConfiguration());        
    }

    public void init() {
        try {
            this.serverSocket = new ServerSocket(trackerContext.getConfiguration().getLocalPort());
        } catch (IOException e) {
            throw new RuntimeException("Cant init agent tracker server!", e);
        }
        trackerAgent.registerEventListener(deviceBootstrapProcessor, deviceBinder);
        for (DeviceCredentials deviceCredentials : trackerContext.getDeviceCredentials()) {
            try {
                deviceBinder.bind(deviceCredentials.getImei());
            } catch (Exception e) {
                logger.error("Failed to initialize device: " + deviceCredentials.getImei());
            }
        }
    }
    
    public void destroy() {
        running = false;
        if(serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.warn("Problem occured trying close server socket: " + e.getMessage());
            }
        }
    }

    @Override
    public void run() {
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
            client.setSoTimeout(trackerContext.getConfiguration().getClientTimeout());
            logger.debug("Accepted connection from {}, launching worker thread.", client.getRemoteSocketAddress());
            requestsExecutor.execute(new RequestHandler(trackerAgent, reportsExecutor, client));
        } catch (Exception e) {
            logger.error("Error while processing:", e);
        }
    }

    public TrackerAgent getTrackerAgent() {
        return trackerAgent;
    }
}
