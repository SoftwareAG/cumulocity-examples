package c8y.trackeragent.server;

import c8y.trackeragent.tracker.ConnectedTracker;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Objects;

@Slf4j
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

    public boolean hasImei(String imei) {
        return Objects.equals(imei, connectionDetails.getImei());
    }

    public boolean hasChannel(SocketChannel channel) {
        return Objects.equals(connectionDetails.getChannel(), channel);
    }

    public void close() {
        try {
            connectionDetails.getChannel().close();
            log.info("Removed and closed legacy connection for imei: {}.", connectionDetails.getImei());
        } catch (IOException e) {
            log.error("Failed to close device connection", e);
        }
    }

    @Override
    public String toString() {
        return connectionDetails.toString();
    }
}