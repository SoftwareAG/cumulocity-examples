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

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ConnectionsContainer {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionsContainer.class);

    private final Map<String, ActiveConnection> connectionsPerImei = new ConcurrentHashMap<>();

    private final Map<SocketChannel, ActiveConnection> connections = new ConcurrentHashMap<>();

    public ActiveConnection get(SocketChannel channel) {
        return connections.get(channel);
    }

    public void register(String imei, SocketChannel channel) {
        ActiveConnection oldConnectionForImei = connectionsPerImei.get(imei);
        removeAndCloseLegacyConnection(imei, channel, oldConnectionForImei);
        registerNewConnectionPerImei(imei, channel);
    }

    private void registerNewConnectionPerImei(String imei, SocketChannel channel) {
        for (ActiveConnection connection : connections.values()) {
            if (connection.getConnectionDetails().getImei() != null
                    && connection.getConnectionDetails().equals(imei)
                    && connection.getConnectionDetails().getChannel().equals(channel)) {
                connectionsPerImei.put(imei, connection);
                log.info("New connection for imei {} registered.", imei);
            }
        }
    }

    private void removeAndCloseLegacyConnection(String imei, SocketChannel channel, ActiveConnection oldConnectionForImei) {
        if (oldConnectionForImei != null
                && !oldConnectionForImei.getConnectionDetails().getChannel().equals(channel)) {
            connectionsPerImei.remove(imei);
            connections.remove(oldConnectionForImei.getConnectionDetails().getChannel());
            try {
                oldConnectionForImei.getConnectionDetails().getChannel().close();
                log.info("Removed and closed legacy connection for imei: {}.", imei);
            } catch (IOException e) {
                log.error("Failed to close device connection", e);
            }
        }
        if (oldConnectionForImei == null) {
            Iterator<ActiveConnection> iterator = connections.values().iterator();
            while (iterator.hasNext()) {
                ActiveConnection activeConnection = iterator.next();
                if (activeConnection.getConnectionDetails().getImei() != null
                        && activeConnection.getConnectionDetails().equals(imei)
                        && !activeConnection.getConnectionDetails().getChannel().equals(channel)) {
                    connections.remove(oldConnectionForImei.getConnectionDetails().getChannel());
                    try {
                        oldConnectionForImei.getConnectionDetails().getChannel().close();
                        log.info("Removed and closed legacy connection for imei: {}.", imei);
                    } catch (IOException e) {
                        log.error("Failed to close device connection", e);
                    }
                }
            }
        }
    }

    public void add(ActiveConnection connection) {
        logger.info("Store connection: {}", connection);
        connections.put(connection.getConnectionDetails().getChannel(), connection);
        logger.info("Number of connections: ", connections.size());
    }

    public ActiveConnection get(String imei) {
        return connectionsPerImei.get(imei);
    }

    protected Collection<ActiveConnection> getAll() {
        return connections.values();
    }

    public ActiveConnection next() {
        Iterator<ActiveConnection> it = connections.values().iterator();
        while (it.hasNext()) {
            ActiveConnection connection = it.next();
            if (!connection.getConnectionDetails().getChannel().isOpen()) {
                logger.info("Found closed connection in container: {}, removing it", connection);
                it.remove();
                remove(connection.getConnectionDetails().getImei());
                continue;
            }
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

    public void remove(String imei) {
        if (StringUtils.isEmpty(imei)) {
            return;
        }
        connectionsPerImei.remove(imei);
    }

    public void remove(SocketChannel channel) {
        ActiveConnection activeConnection = connections.get(channel);
        if (activeConnection == null) {
            logger.warn("Connection for channel: {} has been already removed.", channel);
        } else {
            logger.info("Remove connection: {}", activeConnection);
            connections.remove(channel);
            remove(activeConnection.getConnectionDetails().getImei());
        }
    }
}
