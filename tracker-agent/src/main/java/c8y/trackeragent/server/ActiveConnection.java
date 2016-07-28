package c8y.trackeragent.server;

import c8y.trackeragent.tracker.ConnectedTracker;

public class ActiveConnection {

    private final ConnectionDetails connectionDetails;
    private final ConnectedTracker connectedTracker;
    private final ReportBuffer reportBuffer;
    private boolean processing;

    public ActiveConnection(ConnectionDetails connectionDetails, ConnectedTracker reportExecutor, ReportBuffer reportBuffer) {
        this.connectionDetails = connectionDetails;
        this.connectedTracker = reportExecutor;
        this.reportBuffer = reportBuffer;
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

}