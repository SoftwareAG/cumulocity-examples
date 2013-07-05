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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.Socket;

import org.junit.Test;

import com.cumulocity.sdk.client.SDKException;

public class QueclinkTrackerReaderTest {

	@Test
	public void testSinglePositionReport() throws IOException, SDKException {
		InputStream is = getClass().getResourceAsStream(
				"/singlepositionreport.txt");
		qtr.processReports(is);
		
		verify(trackerMgr).locationUpdate("135790246811220",
				new BigDecimal("31.222073"), new BigDecimal("121.354335"),
				new BigDecimal("70.0"));
	}

	private TrackerManager trackerMgr = mock(TrackerManager.class);
	private Socket client = mock(Socket.class);
	private QueclinkTrackerReader qtr = new QueclinkTrackerReader(client,
			trackerMgr);
}
