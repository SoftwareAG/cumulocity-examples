package c8y.trackeragent.protocol.mapping;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.protocol.coban.ConnectedCobanTracker;
import c8y.trackeragent.protocol.gl200.ConnectedGL200Tracker;
import c8y.trackeragent.protocol.telic.ConnectedTelicTracker;

import com.cumulocity.agent.server.context.DeviceContextService;

@Component
public class TrackerFactory {

    private static final Logger logger = LoggerFactory.getLogger(TrackerFactory.class);

    private static final int HASH_ASCII_CODE = 35;

    private final TrackerAgent trackerAgent;
    private final DeviceContextService contextService;
    
    @Autowired
    public TrackerFactory(TrackerAgent trackerAgent, DeviceContextService contextService) {
        this.trackerAgent = trackerAgent;
        this.contextService = contextService;
    }

    public ConnectedTracker getTracker(Socket client) throws IOException {
        logger.debug("peek tracker for new connection...");
        InputStream bis = asInput(client);
        byte[] markingBytes = firstBytes(bis, 1);
        if (markingBytes[0] >= '0' && markingBytes[0] <= '9') {
            return new ConnectedTelicTracker(client, bis, trackerAgent, contextService);
        } else if (markingBytes[0] == HASH_ASCII_CODE) {
            return new ConnectedCobanTracker(client, bis, trackerAgent, contextService);
        } else {
            return new ConnectedGL200Tracker(client, bis, trackerAgent, contextService);
        }
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

}
