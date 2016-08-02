package c8y.trackeragent.server;

import c8y.trackeragent.tracker.ConnectedTracker;

public class ActiveConnection {

    private final ConnectionDetails connectionDetails;
    private final ConnectedTracker connectedTracker;
    private final ReportBuffer reportBuffer = new ReportBuffer();
    private boolean processing;

    public ActiveConnection(ConnectionDetails connectionDetails, ConnectedTracker reportExecutor) {
        this.connectionDetails = connectionDetails;
        this.connectedTracker = reportExecutor;
    }

    public ConnectedTracker getConnectedTracker() {
        return connectedTracker;
    }

    public ReportBuffer getReportBuffer() {
        return reportBuffer;
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

    @Override
    public String toString() {
        return "ActiveConnection [connectionDetails=" + connectionDetails + "]";
    }
}