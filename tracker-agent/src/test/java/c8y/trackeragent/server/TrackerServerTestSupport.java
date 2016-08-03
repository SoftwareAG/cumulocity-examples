package c8y.trackeragent.server;

import static c8y.trackeragent.utils.ByteHelper.getString;
import static java.util.Arrays.asList;
import static java.util.Collections.synchronizedList;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.server.TrackerServerEvent.ReadDataEvent;
import c8y.trackeragent.server.WritersProvider.Writer;
import c8y.trackeragent.tracker.ConnectedTracker;
import c8y.trackeragent.tracker.ConnectedTrackerFactory;

public abstract class TrackerServerTestSupport {
    
    private static final Logger logger = LoggerFactory.getLogger(TrackerServerTestSupport.class);

    protected static final int PORT = 5100;
    
    private TrackerServer server;
    private final ExecutorService executorService = newFixedThreadPool(1);
    protected CountDownLatch reportExecutorLatch;
    protected final List<TestConnectedTrackerImpl> executors = synchronizedList(new ArrayList<TestConnectedTrackerImpl>());
    protected final ConnectionsContainer connectionsContainer = new ConnectionsContainer();
    protected ConnectedTracker customTracker = null;
    protected final WritersProvider writersProvider = new WritersProvider(PORT);
    
    @Before
    public void before() throws Exception {
        reportExecutorLatch = new CountDownLatch(0);
        TrackerServerEventHandler eventHandler = new TrackerServerEventHandler(new TestConnectedTrackerFactoryImpl(), connectionsContainer);
        eventHandler.init();
        server = new TrackerServer(eventHandler);
        server.start(PORT);
        executorService.execute(server);
        writersProvider.start();
    }
    
    @After
    public void after() throws IOException {
        server.close();
        writersProvider.stop();
    }
    
    protected Writer newWriter() throws Exception {
        return writersProvider.newWriter();
    }
    
    protected void assertThatReportsHandled(String... reports) {
        Object[] expected = new Object[reports.length];
        for (int index = 0; index < reports.length; index++) {
            expected[index] = new TestConnectedTrackerImpl(reports[index]);
        }
        assertThat(executors).contains(expected);
    }
    
    protected void setCountOfExpectedReports(int count) {
        reportExecutorLatch = new CountDownLatch(count);
    }
    
    protected void waitForReports() throws InterruptedException {
        reportExecutorLatch.await(10, TimeUnit.SECONDS);
    }
    
    protected class TestConnectedTrackerFactoryImpl implements ConnectedTrackerFactory {
        
        @Override
        public ConnectedTracker create(ReadDataEvent readData) {
            TestConnectedTrackerImpl result = new TestConnectedTrackerImpl();
            logger.info("Created executor for data " + getString(readData.getData()));
            executors.add(result);
            logger.info("Total executors " + executors.size());
            return result;
        }
    }
    
    protected class TestConnectedTrackerImpl extends DummyConnectedTracker {
        
        private final List<String> processed;
        private ConnectionDetails connectionDetails;
        
        public TestConnectedTrackerImpl(String... processed) {
            this.processed = new ArrayList<String>();
            this.processed.addAll(asList(processed));
        }
        
        @Override
        public void executeReports(ConnectionDetails connectionDetails, byte[] reports) {
            this.connectionDetails = connectionDetails;
            logger.info("Handled report: \'{}\'", getString(reports));
            if (customTracker != null) {
                customTracker.executeReports(connectionDetails, reports);
            }
            String reportsStr = getString(reports);
            List<String> reportList = asList(reportsStr.split(";"));
            for (String report : reportList) {
                processed.add(report);
                reportExecutorLatch.countDown();
            }
        }

        public List<String> getProcessed() {
            return processed;
        }
        
        public ConnectionDetails getConnectionDetails() {
            return connectionDetails;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((processed == null) ? 0 : processed.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TestConnectedTrackerImpl other = (TestConnectedTrackerImpl) obj;
            if (processed == null) {
                if (other.processed != null)
                    return false;
            } else if (!processed.equals(other.processed))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return processed.toString();
        }
    }

    protected class DummyConnectedTracker implements ConnectedTracker {

        @Override
        public void executeOperation(OperationContext operation) throws Exception {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void executeReports(ConnectionDetails connectionDetails, byte[] reports) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public TrackingProtocol getTrackingProtocol() {
            return TrackingProtocol.COBAN;
        }
    }

    
}
