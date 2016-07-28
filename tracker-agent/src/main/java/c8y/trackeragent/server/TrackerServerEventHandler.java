package c8y.trackeragent.server;

import static java.util.concurrent.Executors.newFixedThreadPool;

import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.server.TrackerServerEvent.CloseConnectionEvent;
import c8y.trackeragent.server.TrackerServerEvent.ReadDataEvent;
import c8y.trackeragent.server.writer.OutWriter;
import c8y.trackeragent.server.writer.OutWriterImpl;
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
            connectionsContainer.remove(closeConnectionEvent.getChannel());
        }
    }

    private ActiveConnection getActiveConnection(ReadDataEvent readEvent) throws Exception {
        ActiveConnection connection = connectionsContainer.get(readEvent.getChannel());
        if (connection == null) {
            connection = createConnection(readEvent);
            connectionsContainer.store(connection);
        }
        return connection;
    }

    private ActiveConnection createConnection(ReadDataEvent readEvent) throws Exception {
        ConnectedTracker connectedTracker = connectedTrackerFactory.create(readEvent);
        TrackingProtocol trackingProtocol = connectedTracker.getTrackingProtocol();
        ReportBuffer reportBuffer = new ReportBuffer(trackingProtocol.getReportSeparator());
        OutWriter outWriter = new OutWriterImpl(readEvent.getServer(), readEvent.getChannel());
        ConnectionDetails connectionDetails = new ConnectionDetails(trackingProtocol, outWriter, readEvent.getChannel());
        return new ActiveConnection(connectionDetails, connectedTracker, reportBuffer);
    }

    @Override
    public ActiveConnection next() {
        synchronized (monitor) {
            return connectionsContainer.next();
        }
    }
}
