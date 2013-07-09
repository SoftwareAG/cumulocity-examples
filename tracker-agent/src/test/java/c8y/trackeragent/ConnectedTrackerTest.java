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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

public class ConnectedTrackerTest {
	public static final String REPORT1 = "field1|field2";
	public static final String REPORT2 = "field3|field4";
	
	@Before
	public void setup() throws IOException {
		List<Object> fragments = new ArrayList<Object>();
		fragments.add(translator);
		fragments.add(parser);
		tracker = new ConnectedTracker(client, fragments, GL200Constants.REPORT_SEP, GL200Constants.FIELD_SEP);
		tracker.setOut(out);		
	}
	
	@Test
	public void singleReportProcessing() throws SDKException {
		String[] dummyReport = null;
		when(parser.parse(dummyReport)).thenReturn("imei");
		
		tracker.processReport(dummyReport);
		
		verify(parser).parse(dummyReport);
		verifyZeroInteractions(translator);
		assertEquals(tracker, ConnectionRegistry.instance().get("imei"));
	}
	
	@Test
	public void reportReading() throws IOException {
		String reports = REPORT1 + GL200Constants.REPORT_SEP + REPORT2 + GL200Constants.REPORT_SEP;
		
		try (ByteArrayInputStream is = new ByteArrayInputStream(reports.getBytes(StandardCharsets.US_ASCII))) {
			String report = tracker.readReport(is);
			assertEquals(REPORT1, report);
			report = tracker.readReport(is);
			assertEquals(REPORT2, report);
			report = tracker.readReport(is);
			assertNull(report);
		}
	}

	@Test
	public void continuousReportProcessing() throws IOException, SDKException {
		when(parser.parse(any(String[].class))).thenReturn("imei");
		
		String reports = REPORT1 + GL200Constants.REPORT_SEP + REPORT2 + GL200Constants.REPORT_SEP;
		
		try (ByteArrayInputStream is = new ByteArrayInputStream(reports.getBytes(StandardCharsets.US_ASCII))) {
			tracker.processReports(is);
		}
		
		verify(parser).parse(REPORT1.split(GL200Constants.FIELD_SEP));
		verify(parser).parse(REPORT2.split(GL200Constants.FIELD_SEP));
		verifyZeroInteractions(translator);
		assertEquals(tracker, ConnectionRegistry.instance().get("imei"));		
	}
	
	@Test
	public void testExecute() throws IOException {
		String translation = "translation";
		
		OperationRepresentation operation = mock(OperationRepresentation.class);
		when(translator.translate(operation)).thenReturn(translation);
		
		tracker.execute(operation);
		
		verifyZeroInteractions(parser);
		verify(translator).translate(operation);
		verify(out).write(translation.getBytes(StandardCharsets.US_ASCII));
	}
	
	private Socket client = mock(Socket.class);
	private OutputStream out = mock(OutputStream.class);
	private Translator translator = mock(Translator.class);
	private Parser parser = mock(Parser.class);
	private ConnectedTracker tracker;
}
