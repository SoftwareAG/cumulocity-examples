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
