/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.server;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.server.WritersProvider.Writer;

public class TrackerServerFuzzyTest extends TrackerServerTestSupport {

    private static final int TOTAL_WRITERS = 50;
    private static final int TOTAL_REPORST_PER_WRITER = 20;
    private final List<String> sentReports = new ArrayList<String>();

    @Before
    public void before() throws Exception {
        super.before();
        writersProvider.start(TOTAL_WRITERS);
    }

    @Test
    public void shouldReadManyReports() throws Exception {
        reportExecutorLatch = new CountDownLatch(TOTAL_WRITERS * TOTAL_REPORST_PER_WRITER);
        List<Writer> writers = writersProvider.getWriters();
        for (int reportNo = 0; reportNo < TOTAL_REPORST_PER_WRITER; reportNo++) {
            String report = "" + reportNo;
            sentReports.add(report);
            for (Writer writer : writers) {
                writer.write(report + ";");
            }
            Thread.sleep(100);
        }

        reportExecutorLatch.await(15, TimeUnit.SECONDS);
        assertThat(reportExecutorLatch.getCount()).isEqualTo(0L);
        for (TestConnectedTrackerImpl executor : getExecutors()) {
            assertThat(executor.getProcessed()).hasSize(TOTAL_REPORST_PER_WRITER);
            // Order should not matter and all sentReports are unique.
            for (String msg: sentReports)
                assertThat(executor.getProcessed().contains(msg)).isTrue();
        }
    }

}
