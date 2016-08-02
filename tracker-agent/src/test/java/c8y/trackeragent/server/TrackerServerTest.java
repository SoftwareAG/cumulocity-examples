package c8y.trackeragent.server;

import static c8y.trackeragent.utils.ByteHelper.getString;
import static java.lang.Thread.sleep;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackerServerTest extends TrackerServerTestSupport {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(TrackerServerTest.class);

    private SocketWriter writer1;
    private SocketWriter writer2;
    private SocketWriter writer3;

    @Before
    public void before() throws Exception {
        super.before();
        writer1 = newWriter();
        writer2 = newWriter();
        writer3 = newWriter();
    }

    @Test
    public void shouldReadData() throws Exception {
        setCountOfExpectedReports(3);
        writer1.push("#ABC");
        writer2.push("$123");
        writer3.push("abcd");
        
        waitForReports();

        assertThatReportsHandled("#ABC");
        assertThatReportsHandled("$123");
        assertThatReportsHandled("abcd");
    }

    @Test
    public void shouldHandleClose() throws Exception {
        writer1.push("ABC");
        sleep(100);
        
        assertThat(connectionsContainer.getAll()).hasSize(1);
        
        writer1.stop();
        sleep(100);
        
        assertThat(connectionsContainer.getAll()).isEmpty();
    }

    @Test
    public void shouldRemeberConnectionParameter() throws Exception {
        customTracker = new DummyConnectedTracker() {

            @Override
            public void executeReports(ConnectionDetails connectionDetails, byte[] reports) {
                if (getString(reports).equals("FIRST_REPORT")) {
                    connectionDetails.getParams().put("connection_parameter", 123);
                }
            }
        };

        setCountOfExpectedReports(2);
        writer1.push("FIRST_REPORT");
        writer1.push("SECOND_REPORT");
        waitForReports();
        
        assertThat(executors).hasSize(1);
        assertThat(executors.get(0).getConnectionDetails().getParams().get("connection_parameter")).isEqualTo(123);
    }

}
