package c8y.trackeragent.tracker;

import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.server.ConnectionDetails;

public interface ConnectedTracker {
    
    void executeOperation(OperationContext operation) throws Exception;

    void executeReport(ConnectionDetails connectionDetails, String report);

    TrackingProtocol getTrackingProtocol();
}
