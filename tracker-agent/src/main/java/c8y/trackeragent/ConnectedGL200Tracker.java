package c8y.trackeragent;

import java.io.InputStream;
import java.net.Socket;

public class ConnectedGL200Tracker extends ConnectedTracker {

    private static final String PASSWORD = "gl200";

    public ConnectedGL200Tracker(Socket client, InputStream is, TrackerAgent trackerAgent) {

        super(client, is, GL200Constants.REPORT_SEP, GL200Constants.FIELD_SEP, trackerAgent);

        addFragment(new GL200Geofence(trackerAgent, PASSWORD));
        addFragment(new GL200Power(trackerAgent));
        addFragment(new GL200LocationReport(trackerAgent));
        addFragment(new GL200DeviceMotionState(trackerAgent, PASSWORD));
        addFragment(new GL200Fallback(trackerAgent, PASSWORD));
        addFragment(new GL200FuelReport(trackerAgent,PASSWORD));
    }
}
