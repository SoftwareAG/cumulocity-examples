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

package c8y.trackeragent.server;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConnectionsContainer {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionsContainer.class);

    private final Map<SocketChannel, ActiveConnection> connectionsIndex = new ConcurrentHashMap<SocketChannel, ActiveConnection>();
    private final List<ActiveConnection> connections = new ArrayList<ActiveConnection>();

    public ActiveConnection get(SocketChannel channel) {
        return connectionsIndex.get(channel);
    }

    public ActiveConnection get(String imei) {
        for (ActiveConnection connection : connections) {
            if (imei.equals(connection.getConnectionDetails().getImei())) {
                return connection;
            }
        }
        return null;
    }

    public void add(ActiveConnection connection) {
        synchronized (connections) {
            logger.info("Store connection: {}", connection);
            connectionsIndex.put(connection.getConnectionDetails().getChannel(), connection);
            connections.add(connection);
        }
    }

    protected List<ActiveConnection> getAll() {
        return connections;
    }

    public void remove(String imei) {
        ActiveConnection activeConnection = get(imei);
        if (activeConnection == null) {
            logger.warn("Connection for imei: {} have been already removed.", imei);
        } else {
            remove(activeConnection);
        }
    }

    public void remove(SocketChannel channel) {
        ActiveConnection activeConnection = get(channel);
        if (activeConnection == null) {
            logger.warn("Connection foe channel: {} have been already removed.", channel);
        } else {
            remove(activeConnection);
        }
    }

    private void remove(ActiveConnection activeConnection) {
        logger.info("Remove connection: {}", activeConnection);
        synchronized (connections) {
            connections.remove(activeConnection);
            connectionsIndex.remove(activeConnection.getConnectionDetails().getChannel());
        }
    }

    public ActiveConnection next() {
        for (ActiveConnection connection : connections) {
            if (connection.isProcessing()) {
                continue;
            }
            if (connection.getReportBuffer().isEmpty()) {
                continue;
            }
            connection.setProcessing(true);
            return connection;
        }
        return null;
    }

}
