package c8y.trackeragent.protocol.coban.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import c8y.SupportedOperations;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.coban.device.CobanDevice;

public class CobanSupport implements CobanFragment {
    private static final Logger logger = LoggerFactory.getLogger(CobanSupport.class);

    protected static final String OPERATION_FRAGMENT_SERVER_COMMAND = "serverCommand";

    protected final TrackerAgent trackerAgent;

    public CobanSupport(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
    }

    protected CobanDevice getCobanDevice(String imei) {
        return getTrackerDevice(imei).getCobanDevice();
    }

    protected TrackerDevice getTrackerDevice(String imei) {
        TrackerDevice trackerDevice = trackerAgent.getOrCreateTrackerDevice(imei);
        ManagedObjectRepresentation representation = trackerDevice.getManagedObject();

        if (representation != null) {
            SupportedOperations operationsProperty = representation.get(SupportedOperations.class);
            if (operationsProperty == null) {
                operationsProperty = new SupportedOperations();
                representation.set(operationsProperty);
            }

            if (!operationsProperty.contains("c8y_Command")) {
                operationsProperty.add("c8y_Command");
                representation.setLastUpdatedDateTime(null);

                // update device (inventory)
                logger.info("Agent id: {}", trackerDevice.getAgentId());
                trackerDevice.updateMoOfDevice(representation, trackerDevice.getGId());
                logger.info("Device MO updated: {}", representation);
            }
        }

        return trackerDevice;
    }

}
