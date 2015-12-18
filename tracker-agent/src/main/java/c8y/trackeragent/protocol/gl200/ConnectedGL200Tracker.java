package c8y.trackeragent.protocol.gl200;

import java.io.InputStream;
import java.net.Socket;

import com.cumulocity.agent.server.context.DeviceContextService;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.protocol.gl200.parser.GL200DeviceMotionState;
import c8y.trackeragent.protocol.gl200.parser.GL200Fallback;
import c8y.trackeragent.protocol.gl200.parser.GL200Geofence;
import c8y.trackeragent.protocol.gl200.parser.GL200LocationReport;
import c8y.trackeragent.protocol.gl200.parser.GL200Power;

public class ConnectedGL200Tracker extends ConnectedTracker {

    private static final String PASSWORD = "gl200";

    public ConnectedGL200Tracker(Socket client, InputStream is, TrackerAgent trackerAgent, DeviceContextService contextService) {

        super(client, is, GL200Constants.REPORT_SEP, GL200Constants.FIELD_SEP, trackerAgent, contextService);

        addFragment(new GL200Geofence(trackerAgent, PASSWORD));
        addFragment(new GL200Power(trackerAgent));
        addFragment(new GL200LocationReport(trackerAgent));
        addFragment(new GL200DeviceMotionState(trackerAgent, PASSWORD));
        addFragment(new GL200Fallback(trackerAgent, PASSWORD));
    }
}
