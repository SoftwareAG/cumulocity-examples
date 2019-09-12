package c8y.trackeragent.server;

import c8y.trackeragent.tracker.ConnectedTracker;
import lombok.Getter;

@Getter
public class IncomingMessage {
    private final ConnectionDetails connectionDetails;
    private final ConnectedTracker connectedTracker;
    private final byte[] msg;

    public IncomingMessage(ConnectionDetails connectionDetails, ConnectedTracker connectedTracker, byte[] msg) {
        this.connectionDetails = connectionDetails;
        this.connectedTracker = connectedTracker;
        this.msg = msg;
    }
}
