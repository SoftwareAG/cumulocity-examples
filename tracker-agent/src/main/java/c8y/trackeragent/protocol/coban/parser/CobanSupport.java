package c8y.trackeragent.protocol.coban.parser;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.coban.device.CobanDevice;

public class CobanSupport implements CobanFragment {
    
    protected static final String OPERATION_FRAGMENT_SERVER_COMMAND = "serverCommand";
    
    protected final TrackerAgent trackerAgent;
    
    public CobanSupport(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
    }

    protected CobanDevice getCobanDevice(String imei) {
        return trackerAgent.getOrCreateTrackerDevice(imei).getCobanDevice();
    }
    
    protected TrackerDevice getTrackerDevice(String imei) {
        return trackerAgent.getOrCreateTrackerDevice(imei);
    }

}
