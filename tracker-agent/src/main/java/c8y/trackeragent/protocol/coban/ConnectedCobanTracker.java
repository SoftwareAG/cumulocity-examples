package c8y.trackeragent.protocol.coban;

import java.io.InputStream;
import java.net.Socket;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.protocol.coban.parser.HeartbeatCobanParser;
import c8y.trackeragent.protocol.coban.parser.LogonCobanParser;
import c8y.trackeragent.protocol.coban.parser.PositionUpdateCobanParser;

public class ConnectedCobanTracker extends ConnectedTracker {

    public ConnectedCobanTracker(Socket client, InputStream bis, TrackerAgent trackerAgent) {
        super(client, bis, CobanConstants.REPORT_SEP, CobanConstants.FIELD_SEP, trackerAgent);
        addFragment(new LogonCobanParser(trackerAgent));
        addFragment(new HeartbeatCobanParser(trackerAgent));
        addFragment(new PositionUpdateCobanParser(trackerAgent));
    }

}
