/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.server;

import static c8y.trackeragent.utils.ByteHelper.getString;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.server.WritersProvider.Writer;

@Ignore
public class TrackerServerTest extends TrackerServerTestSupport {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(TrackerServerTest.class);

    private Writer writer1;
    private Writer writer2;
    private Writer writer3;

    @Before
    public void before() throws Exception {
        super.before();
        writersProvider.start(3);
        writer1 = writersProvider.getWriter(0);
        writer2 = writersProvider.getWriter(1);
        writer3 = writersProvider.getWriter(2);
    }

    @Test
    public void shouldReadData() throws Exception {
        setCountOfExpectedReports(3);
        writer1.write("#ABC");
        writer2.write("$123");
        writer3.write("abcd");
        
        waitForReports();

        assertThatReportsHandled("#ABC", "$123", "abcd");
    }

    @Test
    public void shouldHandleClose() throws Exception {
        setCountOfExpectedReports(1);
        writer1.write("ABC");
        
        waitForReports();
        
        writer1.stop();
        sleep(500);
        
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
        writer1.write("FIRST_REPORT");
        Thread.sleep(500);
        writer1.write("SECOND_REPORT");
        waitForReports();
        
        assertThat(getExecutors()).hasSize(1);
        assertThat(getExecutors().get(0).getConnectionDetails().getParams().get("connection_parameter")).isEqualTo(123);
    }

}
