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
import c8y.trackeragent.utils.ByteHelper;

public class IncomingMessage {
    private final ConnectionDetails connectionDetails;
    private final ConnectedTracker connectedTracker;
    private final byte[] msg;

    public IncomingMessage(ConnectionDetails connectionDetails, ConnectedTracker connectedTracker, byte[] msg) {
        this.connectionDetails = connectionDetails;
        this.connectedTracker = connectedTracker;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "IncomingMessage{" +
                "connectionDetails=" + connectionDetails +
                ", connectedTracker=" + connectedTracker +
                ", msg=0x" + ByteHelper.toHexString(msg) +
                '}';
    }

    public void process() {
        connectionDetails.resetImei();
        connectedTracker.executeReports(connectionDetails, msg);
    }
}
