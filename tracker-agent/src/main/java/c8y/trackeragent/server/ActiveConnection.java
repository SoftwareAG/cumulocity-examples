package c8y.trackeragent.server;

import c8y.trackeragent.tracker.ConnectedTracker;

public class ActiveConnection {

    private final ConnectionDetails connectionDetails;
    private final ConnectedTracker connectedTracker;
    private final DataBuffer dataBuffer;
    private boolean processing;

    public ActiveConnection(ConnectionDetails connectionDetails, ConnectedTracker reportExecutor, DataBuffer dataBuffer) {
        this.connectionDetails = connectionDetails;
        this.connectedTracker = reportExecutor;
        this.dataBuffer = dataBuffer;
    }

    public ConnectedTracker getConnectedTracker() {
        return connectedTracker;
    }

    public DataBuffer getDataBuffer() {
        return dataBuffer;
    }

    public boolean isProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public ConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }

}