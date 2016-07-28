package c8y.trackeragent.server;

import static java.util.concurrent.Executors.newFixedThreadPool;

import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.server.TrackerServerEvent.CloseConnectionEvent;
import c8y.trackeragent.server.TrackerServerEvent.ReadDataEvent;
import c8y.trackeragent.tracker.ConnectedTracker;
import c8y.trackeragent.tracker.ConnectedTrackerFactory;

@Component
public class TrackerServerEventHandler implements ActiveConnectionProvider {

    private static final Logger logger = LoggerFactory.getLogger(TrackerServerEventHandler.class);
    private static final int NUMBER_OF_WORKERS = 10;

    private final ExecutorService workers = newFixedThreadPool(NUMBER_OF_WORKERS);
    private final ConnectedTrackerFactory connectedTrackerFactory;    
    private final ConnectionsContainer connectionsContainer;    
    private final Object monitor = new Object();

    @Autowired
    public TrackerServerEventHandler(ConnectedTrackerFactory connectedTrackerFactory, ConnectionsContainer connectionsContainer) {
        this.connectedTrackerFactory = connectedTrackerFactory;
        this.connectionsContainer = connectionsContainer;
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < NUMBER_OF_WORKERS; i++) {
            ReaderWorker worker = new ReaderWorker(this);
            workers.execute(worker);
        }
    }

    public void handle(ReadDataEvent readDataEvent) {
        try {
            synchronized (monitor) {
                ActiveConnection connection = getActiveConnection(readDataEvent);
                connection.getReportBuffer().append(readDataEvent.getData(), readDataEvent.getNumRead());
            }
        } catch (Exception e) {
            logger.error("Exception handling read event " + readDataEvent, e);
        }
    }
    
    public void handle(CloseConnectionEvent closeConnectionEvent) {
        synchronized (monitor) {
            logger.info("Close connection for {}.", closeConnectionEvent.getConnectionDetails());
            connectionsContainer.remove(closeConnectionEvent.getConnectionDetails());
        }
    }


    private ActiveConnection getActiveConnection(ReadDataEvent readEvent) throws Exception {
        ActiveConnection connection = connectionsContainer.get(readEvent.getConnectionDetails());
        if (connection == null) {
            ConnectedTracker connectedTracker = connectedTrackerFactory.create(readEvent);
            ReportBuffer reportBuffer = new ReportBuffer(connectedTracker.getReportSeparator());
            connection = new ActiveConnection(readEvent.getConnectionDetails(), connectedTracker, reportBuffer);
            connectionsContainer.put(readEvent.getConnectionDetails(), connection);
        }
        return connection;
    }

    @Override
    public ActiveConnection next() {
        synchronized (monitor) {
            return connectionsContainer.next();
        }
    }

    
    

}
