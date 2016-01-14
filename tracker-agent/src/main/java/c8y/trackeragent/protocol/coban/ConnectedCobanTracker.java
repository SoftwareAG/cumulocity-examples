package c8y.trackeragent.protocol.coban;

import java.io.InputStream;
import java.net.Socket;

import com.cumulocity.agent.server.context.DeviceContextService;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.protocol.coban.parser.AlarmCobanParser;
import c8y.trackeragent.protocol.coban.parser.CobanConfigRefreshTranslator;
import c8y.trackeragent.protocol.coban.parser.HeartbeatCobanParser;
import c8y.trackeragent.protocol.coban.parser.LogonCobanParser;
import c8y.trackeragent.protocol.coban.parser.PositionUpdateCobanParser;
import c8y.trackeragent.protocol.coban.service.AlarmService;
import c8y.trackeragent.service.MeasurementService;

public class ConnectedCobanTracker extends ConnectedTracker {
    
    public ConnectedCobanTracker(Socket client, InputStream bis, TrackerAgent trackerAgent, DeviceContextService contextService) {
        super(client, bis, CobanConstants.REPORT_SEP, CobanConstants.FIELD_SEP, trackerAgent, contextService);
        CobanServerMessages serverMessages = new CobanServerMessages();
        AlarmService alarmService = new AlarmService();
        MeasurementService measurementService = new MeasurementService();
        addFragment(new LogonCobanParser(trackerAgent, serverMessages));
        addFragment(new HeartbeatCobanParser(trackerAgent, serverMessages));
        addFragment(new PositionUpdateCobanParser(trackerAgent, serverMessages, alarmService, measurementService));
        addFragment(new AlarmCobanParser(trackerAgent, alarmService));
        addFragment(new CobanConfigRefreshTranslator(trackerAgent, serverMessages));
    }

}
