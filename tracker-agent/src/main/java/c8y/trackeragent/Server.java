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
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.utils.TrackerContext;
import c8y.trackeragent.utils.TrackerContextFactory;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

/**
 * The server listens to connections from devices and starts threads for
 * handling device communication for particular types of devices. (Currently,
 * only GL200.)
 */
public class Server implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private final ServerSocket serverSocket;
    private final TrackerContext trackerContext;
    private final TrackerAgent trackerAgent;
    private final Collection<OperationDispatcher> operationDispatchers = new ArrayList<>();

    public Server() throws IOException {
        this.trackerContext = TrackerContextFactory.createTrackerContext();
        this.trackerAgent = new TrackerAgent(trackerContext);
        this.serverSocket = new ServerSocket(trackerContext.getLocalSocketPort());
    }
    
    public void init() {
        startPlatformsUtilities();        
    }

    @Override
    public void run() {
        while (true) {
            accept();
        }
    }
    
    private void startPlatformsUtilities() {
        Collection<TrackerPlatform> platforms = trackerContext.getPlatforms();
        for (TrackerPlatform trackerPlatform : platforms) {
            startPlatformUtilities(trackerPlatform);
        }
    }

    private void startPlatformUtilities(TrackerPlatform trackerPlatform) {
        ManagedObjectRepresentation agent = trackerContext.getOrCreateAgent(trackerPlatform.getTenantId());
        operationDispatchers.add(new OperationDispatcher(trackerPlatform, agent.getId()));
        //new TracelogDriver(trackerPlatform, agent);
        
    }

    private void accept() {
        try {
            logger.debug("Waiting for connection on port {}", serverSocket.getLocalPort());
            Socket client = serverSocket.accept();
            logger.debug("Accepted connection from {}, launching worker thread.", client.getRemoteSocketAddress());

            ConnectedTracker tracker = peekTracker(client);
            new Thread(tracker).start();
        } catch (IOException e) {
            logger.warn("Couldn't connect", e);
        }
    }

    private ConnectedTracker peekTracker(Socket client) throws IOException {
        InputStream bis = asInput(client);
        bis.mark(1);
        int marker = bis.read();
        bis.reset();

        if (marker >= '0' && marker <= '9') {            
            return new ConnectedTelicTracker(client, bis, trackerAgent);
        } else {
            return new ConnectedGL200Tracker(client, bis, trackerAgent);
        }
    }

    private InputStream asInput(Socket client) throws IOException {
        InputStream is = client.getInputStream();
        return new BufferedInputStream(is);
    }

    public TrackerAgent getTrackerAgent() {
        return trackerAgent;
    }

    public TrackerContext getTrackerContext() {
        return trackerContext;
    }
}
