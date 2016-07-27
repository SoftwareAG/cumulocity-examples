package c8y.trackeragent.tracker;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.configuration.TrackerConfiguration;
import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.server.TrackerServerEvent.ReadDataEvent;

@Component
public class ConnectedTrackerFactoryImpl implements ConnectedTrackerFactory {

    private static final Logger logger = LoggerFactory.getLogger(ConnectedTrackerFactoryImpl.class);

    private final TrackerConfiguration config;
    private ListableBeanFactory beanFactory;
    
    @Autowired
    public ConnectedTrackerFactoryImpl(TrackerConfiguration config, ListableBeanFactory beanFactory) {
        this.config = config;
        this.beanFactory = beanFactory;
    }
    
    @Override
    public ConnectedTracker create(ReadDataEvent readData) throws Exception {
        logger.debug("Will peek tracker for new connection...");
        int localPort = readData.getConnectionDetails().getChannel().socket().getLocalPort();
        byte markingByte = readData.getData()[0];
        ConnectedTracker result = create(localPort, markingByte);
        logger.debug("Tracker for new connection: {}", result.getClass().getSimpleName());
        return result;
    }

    private ConnectedTracker create(int localPort, byte markingByte) throws Exception {
        if (localPort == config.getLocalPort1()) {
            return discoverTracker(markingByte, localPort, config.getLocalPort1Protocols());
        } else if (localPort == config.getLocalPort2()) {
            return discoverTracker(markingByte, localPort, config.getLocalPort2Protocols());
        } else {
            throw new RuntimeException("Only support local ports: " + config.getLocalPort1() + ", " + config.getLocalPort2());
        }
    }
    
    private ConnectedTracker discoverTracker(byte markingByte, int localPort, Collection<TrackingProtocol> available) throws Exception {
        for (TrackingProtocol trackerProtocol : available) {
            if (trackerProtocol.accept(markingByte)) {
                return beanFactory.getBean(trackerProtocol.getTrackerClass());
            }
        }
        logger.warn("No matching tracker found for first byte " + markingByte + " on port " + localPort);
        return null;
    }
}
