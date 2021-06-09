/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.server;

import c8y.trackeragent.tracker.ConnectedTracker;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Objects;

@Slf4j
public class ActiveConnection {

    private final ConnectionDetails connectionDetails;
    private final ConnectedTracker connectedTracker;

    public ActiveConnection(ConnectionDetails connectionDetails, ConnectedTracker reportExecutor) {
        this.connectionDetails = connectionDetails;
        this.connectedTracker = reportExecutor;
    }

    public ConnectedTracker getConnectedTracker() {
        return connectedTracker;
    }

    public ConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }

    public boolean hasImei(String imei) {
        return Objects.equals(imei, connectionDetails.getImei());
    }

    public boolean hasChannel(SocketChannel channel) {
        return Objects.equals(connectionDetails.getChannel(), channel);
    }

    public void close() {
        try {
            connectionDetails.getChannel().close();
            log.info("Removed and closed legacy connection for imei: {}.", connectionDetails.getImei());
        } catch (IOException e) {
            log.error("Failed to close device connection", e);
        }
    }

    @Override
    public String toString() {
        return connectionDetails.toString();
    }
}