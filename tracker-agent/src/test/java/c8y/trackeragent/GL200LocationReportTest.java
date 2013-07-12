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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import c8y.Position;

import com.cumulocity.sdk.client.SDKException;

public class GL200LocationReportTest {
	public static final String IMEI = "135790246811220";
	public static final Position POS1 = new Position();
	public static final String CELLID1 = "6141";
	public static final Position POS2 = new Position();
	public static final String CELLID2 = "6142";
	public static final String FIXEDREPSTR = "+RESP:GTFRI,02010B,135790246811220,,0,0,2,1,4.3,92,70.0,121.354335,31.222073,20090 214013254,0460,0000,18d8,6141,00,0,4.3,92,70.0,121.354336,31.222074,20090101000000,04 60,0000,18d8,6142,00,,20090214093254,11F0$";
	public static final String[] FIXEDREP = FIXEDREPSTR
			.split(GL200Constants.FIELD_SEP);

	@Before
	public void setup() throws SDKException {
		gl200fr = new GL200LocationReport(trackerAgent);
		when(trackerAgent.getOrCreate(anyString())).thenReturn(device);

		POS1.setAlt(new BigDecimal("70.0"));
		POS1.setLng(new BigDecimal("121.354335"));
		POS1.setLat(new BigDecimal("31.222073"));
		
		POS2.setAlt(new BigDecimal("70.0"));
		POS2.setLng(new BigDecimal("121.354336"));
		POS2.setLat(new BigDecimal("31.222074"));
	}
	
	@Test
	public void fixedReport() throws SDKException {
		String imei = gl200fr.parse(FIXEDREP);
		
		assertEquals(IMEI, imei);
		verify(trackerAgent).getOrCreate(IMEI);
		
		verify(device).setPosition(POS1);
		verify(device).setCellId(CELLID1);
		
		verify(device).setPosition(POS2);
		verify(device).setCellId(CELLID2);		
	}
	
	@Test 
	public void otherReports() throws SDKException {
		String[] nonsenseReport = { "+NONSENSE" }; 
		String imei = gl200fr.parse(nonsenseReport);
		assertNull(imei);
		
		String[] buffReport = FIXEDREPSTR.split(GL200Constants.FIELD_SEP);
		buffReport[0] = "+BUFF:GTFRI";
		imei = gl200fr.parse(buffReport);
		assertEquals(IMEI, imei);

		String[] pnlReport = FIXEDREPSTR.split(GL200Constants.FIELD_SEP);
		pnlReport[0] = "+BUFF:GTPNL";
		imei = gl200fr.parse(pnlReport);
		assertEquals(IMEI, imei);
		
	}

	private GL200LocationReport gl200fr;
	private TrackerAgent trackerAgent = mock(TrackerAgent.class);
	private TrackerDevice device = mock(TrackerDevice.class);	
}
