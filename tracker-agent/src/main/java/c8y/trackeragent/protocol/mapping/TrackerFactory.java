package c8y.trackeragent.protocol.mapping;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.utils.TrackerConfiguration;

@Component
public class TrackerFactory {

    private static final Logger logger = LoggerFactory.getLogger(TrackerFactory.class);

    private final TrackerConfiguration config;
    private ListableBeanFactory beanFactory;
    
    @Autowired
    public TrackerFactory(TrackerConfiguration config, ListableBeanFactory beanFactory) {
        this.config = config;
        this.beanFactory = beanFactory;
    }

    public ConnectedTracker<?> getTracker(Socket client) throws Exception {
        logger.debug("peek tracker for new connection...");
        if (client.getLocalPort() == config.getLocalPort1()) {
            return discoverTracker(client, config.getLocalPort1Protocols());
        } else if (client.getLocalPort() == config.getLocalPort2()) {
            return discoverTracker(client, config.getLocalPort2Protocols());
        } else {
            throw new RuntimeException("Only support local ports: " + config.getLocalPort1() + ", " + config.getLocalPort2());
        }
    }
    
    private ConnectedTracker<?> discoverTracker(Socket client, Collection<TrackingProtocol> available) throws Exception {
        InputStream in = asInput(client);
        byte[] markingBytes = firstBytes(in, 1);
        for (TrackingProtocol trackerProtocol : available) {
            if (trackerProtocol.accept(markingBytes[0])) {
                return create(trackerProtocol, client, in);
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
    
    private ConnectedTracker<?> create(TrackingProtocol trackerProtocol, Socket client, InputStream in) throws Exception {
        ConnectedTracker<?> tracker = beanFactory.getBean(trackerProtocol.getTrackerClass());
        tracker.init(client, in);
        return tracker;
    }

}
