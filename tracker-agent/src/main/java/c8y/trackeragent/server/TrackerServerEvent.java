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
