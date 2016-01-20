package c8y.trackeragent.protocol.mapping;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.protocol.coban.ConnectedCobanTracker;
import c8y.trackeragent.protocol.gl200.ConnectedGL200Tracker;
import c8y.trackeragent.protocol.rfv16.ConnectedRFV16Tracker;
import c8y.trackeragent.protocol.telic.ConnectedTelicTracker;
import c8y.trackeragent.service.AlarmService;
import c8y.trackeragent.utils.TrackerConfiguration;

import com.cumulocity.agent.server.context.DeviceContextService;

@Component
public class TrackerFactory {

    private static final Logger logger = LoggerFactory.getLogger(TrackerFactory.class);

    private final TrackerAgent trackerAgent;
    private final DeviceContextService contextService;
    private final TrackerConfiguration config;
    private final AlarmService alarmService;
    
    @Autowired
    public TrackerFactory(
            TrackerAgent trackerAgent, 
            DeviceContextService contextService, 
            TrackerConfiguration config, 
            AlarmService alarmService) {
        this.trackerAgent = trackerAgent;
        this.contextService = contextService;
        this.config = config;
        this.alarmService = alarmService;
    }

    public ConnectedTracker getTracker(Socket client) throws IOException {
        logger.debug("peek tracker for new connection...");
        if (client.getLocalPort() == config.getLocalPort1()) {
            return discoverTracker(client, config.getLocalPort1Protocols());
        } else if (client.getLocalPort() == config.getLocalPort2()) {
            return discoverTracker(client, config.getLocalPort2Protocols());
        } else {
            throw new RuntimeException("Only support local ports: " + config.getLocalPort1() + ", " + config.getLocalPort2());
        }
    }
    
    private ConnectedTracker discoverTracker(Socket client, Collection<TrackerProtocol> available) throws IOException {
        InputStream bis = asInput(client);
        byte[] markingBytes = firstBytes(bis, 1);
        for (TrackerProtocol trackerProtocol : available) {
            if (trackerProtocol.accept(markingBytes[0])) {
                return create(client, bis, trackerProtocol);
            }
        }
        logger.warn("No matching tracker found for first byte " + markingBytes[0] + " on port " + client.getLocalPort());
        return null;
    }

    private static InputStream asInput(Socket client) throws IOException {
        InputStream is = client.getInputStream();
        return new BufferedInputStream(is);
    }

    private byte[] firstBytes(InputStream is, int limit) throws IOException {
        byte[] bytes = new byte[limit];
        is.mark(limit);
        int b;
        int index = 0;
        while ((b = is.read()) >= 0 && index < limit) {
            bytes[index] = (byte) b;
            index++;
        }
        is.reset();
        return bytes;
    }
    
    private ConnectedTracker create(Socket client, InputStream bis, TrackerProtocol trackerProtocol) throws IOException {
        switch (trackerProtocol) {
        case TELIC:
            return new ConnectedTelicTracker(client, bis, trackerAgent, contextService);
        case GL200:
            return new ConnectedGL200Tracker(client, bis, trackerAgent, contextService);
        case COBAN:
            return new ConnectedCobanTracker(client, bis, trackerAgent, contextService, alarmService);
        case RFV16:
            return new ConnectedRFV16Tracker(client, bis, trackerAgent, contextService, alarmService);
        default:
            throw new RuntimeException("Cant create connected tracker for name " + trackerProtocol);
        }
    }

}
