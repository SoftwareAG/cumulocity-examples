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

    public void process(){
        connectedTracker.executeReports(connectionDetails, msg);
    }
}
