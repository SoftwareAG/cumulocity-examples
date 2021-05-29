/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.server;

import java.nio.channels.SocketChannel;

public class ChangeRequest {

    private final SocketChannel socket;
    private final int ops;

    public ChangeRequest(SocketChannel socket, int ops) {
        this.socket = socket;
        this.ops = ops;
    }

    public SocketChannel getSocket() {
        return socket;
    }

    public int getOps() {
        return ops;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ChangeRequest [socket=");
        builder.append(socket);
        builder.append(", ops=");
        builder.append(ops);
        builder.append("]");
        return builder.toString();
    }

}
