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

import com.google.common.base.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.collect.FluentIterable.from;

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
        removeAndCloseLegacyConnection(imei, channel);
        registerNewConnectionPerImei(imei, channel);
    }

    private void registerNewConnectionPerImei(String imei, SocketChannel channel) {
        for (ActiveConnection connection : from(connections.values()).filter(byImeiAndChannel(imei, channel))) {
            final ActiveConnection connectionPerImei = connectionsPerImei.get(imei);
            if (connectionPerImei == null || !connectionPerImei.equals(connection)) {
                connectionsPerImei.put(imei, connection);
                log.info("New connection for imei {} registered.", imei);
            }
        }
    }

    private Predicate<ActiveConnection> byImeiAndChannel(final String imei, final SocketChannel channel) {
        return new Predicate<ActiveConnection>() {
            @Override
            public boolean apply(final ActiveConnection connection) {
                return connection.hasImei(imei) && connection.hasChannel(channel);
            }
        };
    }

    private void removeAndCloseLegacyConnection(String imei, SocketChannel channel) {
        ActiveConnection oldConnectionForImei = connectionsPerImei.get(imei);
        if (oldConnectionForImei != null
                && !oldConnectionForImei.hasChannel(channel)) {
            connectionsPerImei.remove(imei);
            connections.remove(oldConnectionForImei.getConnectionDetails().getChannel());
            oldConnectionForImei.close();
            log.info("Removed and close legacy connection for imei {}.", imei);
        }
        Iterator<ActiveConnection> iterator = connections.values().iterator();
        while (iterator.hasNext()) {
            ActiveConnection activeConnection = iterator.next();
            if (activeConnection.hasImei(imei) && !activeConnection.hasChannel(channel)) {
                iterator.remove();
                activeConnection.close();
            }
        }
    }

    public void add(ActiveConnection connection) {
        logger.info("Store connection: {}", connection);
        connections.put(connection.getConnectionDetails().getChannel(), connection);
        logger.info("Number of connections: {}", connections.size());
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
