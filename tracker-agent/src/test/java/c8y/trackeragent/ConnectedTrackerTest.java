/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package c8y.trackeragent;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.ConnectionRegistry;
import c8y.trackeragent.GL200Constants;
import c8y.trackeragent.Parser;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.Translator;
import c8y.trackeragent.event.TrackerAgentEvents;
import c8y.trackeragent.utils.TrackerContext;

import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

public class ConnectedTrackerTest {
    
    public static final String REPORT1 = "field1|field2";
    public static final String REPORT2 = "field3|field4";
    private static final Charset CHARSET = Charset.forName("US-ASCII");

    private Socket client = mock(Socket.class);
    private BufferedInputStream bis = mock(BufferedInputStream.class);
    private OutputStream out = mock(OutputStream.class);
    private Translator translator = mock(Translator.class);
    private Parser parser = mock(Parser.class);
    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerContext trackerContext = mock(TrackerContext.class);
    private ConnectedTracker tracker;

    @Before
    public void setup() throws IOException {
        ConnectionRegistry.instance().remove("imei");
        when(trackerAgent.getContext()).thenReturn(trackerContext);
        tracker = new ConnectedTracker(client, bis, GL200Constants.REPORT_SEP, GL200Constants.FIELD_SEP, trackerAgent);
        tracker.addFragment(translator);
        tracker.addFragment(parser);
        tracker.setOut(out);
    }

    @Test
    public void singleReportProcessing() throws SDKException {
        String[] dummyReport = null;
        when(parser.parse(dummyReport)).thenReturn("imei");
        when(parser.onParsed(dummyReport, "imei")).thenReturn(true);
        when(trackerContext.isDeviceRegistered("imei")).thenReturn(true);

        tracker.processReport(dummyReport);

        verify(parser).parse(dummyReport);
        verify(parser).onParsed(dummyReport, "imei");
        verifyZeroInteractions(translator);
        assertEquals(tracker, ConnectionRegistry.instance().get("imei"));
    }
    
    @Test
    public void singleReportProcessingForNewImei() throws SDKException {
        String[] dummyReport = null;
        when(parser.parse(dummyReport)).thenReturn("imei");
        when(trackerContext.isDeviceRegistered("imei")).thenReturn(false);
        
        tracker.processReport(dummyReport);
        
        assertThat(ConnectionRegistry.instance()).isEmpty();
        verify(tracker.trackerAgent).sendEvent(any(TrackerAgentEvents.NewDeviceEvent.class));
        verifyZeroInteractions(translator);
    }

    @Test
    public void reportReading() throws IOException {
        String reports = REPORT1 + GL200Constants.REPORT_SEP + REPORT2 + GL200Constants.REPORT_SEP;
        ByteArrayInputStream is = null;
        try {
            is = new ByteArrayInputStream(reports.getBytes(CHARSET));
            String report = tracker.readReport(is);
            assertEquals(REPORT1, report);
            report = tracker.readReport(is);
            assertEquals(REPORT2, report);
            report = tracker.readReport(is);
            assertNull(report);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Test
    public void continuousReportProcessing() throws IOException, SDKException {
        when(parser.parse(any(String[].class))).thenReturn("imei");
        when(trackerContext.isDeviceRegistered("imei")).thenReturn(true);

        String reports = REPORT1 + GL200Constants.REPORT_SEP + REPORT2 + GL200Constants.REPORT_SEP;

        ByteArrayInputStream is = null;
        try {
            is = new ByteArrayInputStream(reports.getBytes(CHARSET));
            tracker.processReports(is);
        } finally {
            IOUtils.closeQuietly(is);
        }

        verify(parser).parse(REPORT1.split(GL200Constants.FIELD_SEP));
        verify(parser).parse(REPORT2.split(GL200Constants.FIELD_SEP));
        verifyZeroInteractions(translator);
    }

    @Test
    public void testExecute() throws IOException {
        String translation = "translation";

        OperationRepresentation operation = mock(OperationRepresentation.class);
        when(translator.translate(operation)).thenReturn(translation);

        tracker.execute(operation);

        verifyZeroInteractions(parser);
        verify(translator).translate(operation);
        verify(out).write(translation.getBytes(CHARSET));
    }
}
