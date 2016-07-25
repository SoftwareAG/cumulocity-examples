package c8y.trackeragent.nioserver;

import java.nio.channels.SocketChannel;

public class NioServerEvent {

    private final SocketChannel channel;

    public NioServerEvent(SocketChannel channel) {
        this.channel = channel;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public static class ReadDataEvent extends NioServerEvent {

        private final byte[] data;
        private final int numRead;

        public ReadDataEvent(SocketChannel channel, byte[] data, int numRead) {
            super(channel);
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

}
