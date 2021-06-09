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

public class TrackerServerEvent {

    private final TrackerServer server;
    private final SocketChannel channel;

    public TrackerServerEvent(TrackerServer server, SocketChannel channel) {
        this.server = server;
        this.channel = channel;
    }

    public TrackerServer getServer() {
        return server;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public static class ReadDataEvent extends TrackerServerEvent {

        private final byte[] data;
        private final int numRead;

        public ReadDataEvent(TrackerServer server, SocketChannel channel, byte[] data, int numRead) {
            super(server, channel);
            this.data = data;
            this.numRead = numRead;
        }

        public byte[] getData() {
            return data;
        }

        public int getNumRead() {
            return numRead;
        }

    }

    public static class CloseConnectionEvent extends TrackerServerEvent {

        public CloseConnectionEvent(TrackerServer server, SocketChannel channel) {
            super(server, channel);
        }
    }

}
