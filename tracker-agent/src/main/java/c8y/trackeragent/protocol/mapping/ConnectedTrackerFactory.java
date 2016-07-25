package c8y.trackeragent.protocol.mapping;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.configuration.TrackerConfiguration;
import c8y.trackeragent.nioserver.NioServerEvent.ReadDataEvent;
import c8y.trackeragent.nioserver.ReaderWorkerExecutor;
import c8y.trackeragent.nioserver.ReaderWorkerExecutorFactory;

@Component
public class ConnectedTrackerFactory implements ReaderWorkerExecutorFactory {

    private static final Logger logger = LoggerFactory.getLogger(ConnectedTrackerFactory.class);

    private final TrackerConfiguration config;
    private ListableBeanFactory beanFactory;
    
    @Autowired
    public ConnectedTrackerFactory(TrackerConfiguration config, ListableBeanFactory beanFactory) {
        this.config = config;
        this.beanFactory = beanFactory;
    }
    
    @Override
    public ReaderWorkerExecutor create(ReadDataEvent readData) throws Exception {
        logger.debug("peek tracker for new connection...");
        int localPort = readData.getChannel().socket().getLocalPort();
        byte markingByte = readData.getData()[0];
        return create(localPort, markingByte);
    }

    private ReaderWorkerExecutor create(int localPort, byte markingByte) throws Exception {
        if (localPort == config.getLocalPort1()) {
            return discoverTracker(markingByte, localPort, config.getLocalPort1Protocols());
        } else if (localPort == config.getLocalPort2()) {
            return discoverTracker(markingByte, localPort, config.getLocalPort2Protocols());
        } else {
            throw new RuntimeException("Only support local ports: " + config.getLocalPort1() + ", " + config.getLocalPort2());
        }
    }
    
    private ConnectedTracker<?> discoverTracker(byte markingByte, int localPort, Collection<TrackingProtocol> available) throws Exception {
        for (TrackingProtocol trackerProtocol : available) {
            if (trackerProtocol.accept(markingByte)) {
                return beanFactory.getBean(trackerProtocol.getTrackerClass());
            }
        }
        logger.warn("No matching tracker found for first byte " + markingByte + " on port " + localPort);
        return null;
    }
}
