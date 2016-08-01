package c8y.trackeragent.server;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

public class TrackerServerFuzzyTest extends TrackerServerTestSupport {

    private static final int TOTAL_WRITERS = 50;
    private static final int TOTAL_REPORST_PER_WRITER = 120;
    private final List<SocketWriter> writers = new ArrayList<SocketWriter>();
    private final List<String> sendReports = new ArrayList<String>();

    @Before
    public void before() throws Exception {
        super.before();
        for (int i = 0; i < TOTAL_WRITERS; i++) {
            writers.add(newWriter());
        }
        customTracker = new DummyConnectedTracker() {
            
            Random random = new Random();

            @Override
            public void executeReport(ConnectionDetails connectionDetails, String report) {
                try {
                    Thread.sleep(random.nextInt(5));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
        };
    }

    @Test
    public void shouldReadManyReports() throws Exception {
        reportExecutorLatch = new CountDownLatch(TOTAL_WRITERS * TOTAL_REPORST_PER_WRITER);

        for (int reportNo = 0; reportNo < TOTAL_REPORST_PER_WRITER; reportNo++) {
            String report = "" + reportNo;
            sendReports.add(report);
            for (SocketWriter writer : writers) {
                writer.push(report + ";");
            }
            checkIfConnectionsContaintesCoherence();
        }

        reportExecutorLatch.await(2, TimeUnit.SECONDS);
        assertThat(reportExecutorLatch.getCount()).isEqualTo(0L);
        for (TestConnectedTrackerImpl executor : executors) {
            assertThat(executor.getProcessed()).hasSize(TOTAL_REPORST_PER_WRITER);
            assertThat(executor.getProcessed()).isEqualTo(sendReports);
        }
    }

    private void checkIfConnectionsContaintesCoherence() {
        assertThat(connectionsContainer.getAll()).hasSize(connectionsContainer.getAllIndex().size());
        
    }

}
