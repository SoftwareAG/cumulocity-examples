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

package c8y.trackeragent.protocol.queclink;

import static c8y.trackeragent.protocol.TrackingProtocol.QUECLINK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.cumulocity.sdk.client.SDKException;

import c8y.Mobile;
import c8y.Position;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.parser.QueclinkIgnition;
import c8y.trackeragent.protocol.queclink.parser.QueclinkLocationReport;
import c8y.trackeragent.server.TestConnectionDetails;
import c8y.trackeragent.service.MeasurementService;
import c8y.trackeragent.utils.QueclinkReports;

public class GL200LocationReportTest {
	public static final String IMEI = "135790246811220";
	public static final Position POS1 = new Position();
	public static final String MOBILINFOSTR1 = "0460,0000,18d8,6141,00";
	public static final String MOBILINFOSTR2 = "0460,0000,18d8,6142,00";
	public static final Position POS2 = new Position();
	public static DateTime dateTime;
	public static final String FIXEDREPSTR = "+RESP:GTFRI,02010B,135790246811220,,0,0,2,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,0,4.3,92,70.0,121.354336,31.222074,20090101000000,0460,0000,18d8,6142,00,10,20090214093254,11F0$";	

	public static final String[] FIXEDREP = FIXEDREPSTR
			.split(QUECLINK.getFieldSeparator());

	public static final String DOGREPSTR = "+RESP:GTDOG,02010B,135790246811220,,0,0,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$";
	public static final String[] DOGREP = DOGREPSTR.split(QUECLINK.getFieldSeparator());
	
	private TrackerAgent trackerAgent = mock(TrackerAgent.class);
	private TrackerDevice device = mock(TrackerDevice.class);	
	private MeasurementService measurementService = Mockito.mock(MeasurementService.class);
	private QueclinkIgnition queclinkIgnition = mock(QueclinkIgnition.class);
	private QueclinkLocationReport locationReport = new QueclinkLocationReport(trackerAgent, measurementService, queclinkIgnition);
	private TestConnectionDetails connectionDetails = new TestConnectionDetails();

	@Before
	public void setup() throws SDKException {
		when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);

		POS1.setAlt(new BigDecimal("70.0"));
		POS1.setLng(new BigDecimal("121.354335"));
		POS1.setLat(new BigDecimal("31.222073"));
		
		POS2.setAlt(new BigDecimal("70.0"));
		POS2.setLng(new BigDecimal("121.354336"));
		POS2.setLat(new BigDecimal("31.222074"));
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        dateTime = formatter.parseDateTime("20090214093254");
	}
	
	@Test
	public void testReportWithMultiplePoints() throws SDKException {
		String imei = locationReport.parse(FIXEDREP);
		connectionDetails.setImei(imei);
		locationReport.onParsed(new ReportContext(connectionDetails, FIXEDREP));
		
		assertEquals(IMEI, imei);
		verify(trackerAgent).getOrCreateTrackerDevice(IMEI);
		
		verify(device).setPosition(POS1, QueclinkReports.convertEntryToDateTime("20090214093254"));
		Mobile mobile = generateMobileInfo(MOBILINFOSTR1);
		verify(device).setMobileInfo(mobile.getMcc(), mobile.getMnc(), mobile.getLac(), mobile.getCellId());
		
		verify(device).setPosition(POS2, QueclinkReports.convertEntryToDateTime("20090214093254"));
		mobile = generateMobileInfo(MOBILINFOSTR2);
		verify(device).setMobileInfo(mobile.getMcc(), mobile.getMnc(), mobile.getLac(), mobile.getCellId());
		
		verify(measurementService).createPercentageBatteryLevelMeasurement(new BigDecimal(10), device, dateTime);
	}
	
	@Test
	public void testReportWithSinglePoint() throws SDKException {
	    String imei = locationReport.parse(DOGREP);
	    connectionDetails.setImei(imei);
	    locationReport.onParsed(new ReportContext(connectionDetails, DOGREP));
	    
	    assertEquals(IMEI, imei);
	    verify(trackerAgent).getOrCreateTrackerDevice(IMEI);
	    
	    verify(device).setPosition(POS1, QueclinkReports.convertEntryToDateTime("20090214093254"));
	    Mobile mobile = generateMobileInfo(MOBILINFOSTR1);
	    verify(device).setMobileInfo(mobile.getMcc(), mobile.getMnc(), mobile.getLac(), mobile.getCellId());
	    
	    verify(measurementService).createPercentageBatteryLevelMeasurement(new BigDecimal("2000.0"), device, dateTime);
	}	
	
	@Test 
	public void otherReports() throws SDKException {
		String[] nonsenseReport = { "+NONSENSE" }; 
		String imei = locationReport.parse(nonsenseReport);
		assertNull(imei);
		
		String[] buffReport = FIXEDREPSTR.split(QUECLINK.getFieldSeparator());
		buffReport[0] = "+BUFF:GTFRI";
		imei = locationReport.parse(buffReport);
		assertEquals(IMEI, imei);

		String[] pnlReport = FIXEDREPSTR.split(QUECLINK.getFieldSeparator());
		pnlReport[0] = "+BUFF:GTPNL";
		imei = locationReport.parse(pnlReport);
		assertEquals(IMEI, imei);
		
	}
	
	private Mobile generateMobileInfo(String mobileInfo) {
        String[] mobileData = mobileInfo.split(QUECLINK.getFieldSeparator());
        
        Mobile mobile = new Mobile();
        mobile.setMcc(mobileData[0]);
        mobile.setMnc(mobileData[1]);
        int lacDecimal = Integer.parseInt(mobileData[2], 16);
        mobile.setLac(String.valueOf(lacDecimal));
        int cellDecimal = Integer.parseInt(mobileData[3], 16);
        mobile.setCellId(String.valueOf(cellDecimal));
        
        return mobile;
    }
	
	
}
