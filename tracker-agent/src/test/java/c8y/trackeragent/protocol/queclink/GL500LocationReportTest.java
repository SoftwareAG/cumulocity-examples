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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.sdk.client.SDKException;

import c8y.Mobile;
import c8y.Position;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.parser.QueclinkLocationReport;
import c8y.trackeragent.server.TestConnectionDetails;
import c8y.trackeragent.service.MeasurementService;
import c8y.trackeragent.utils.QueclinkReports;

public class GL500LocationReportTest {
    
    public static final String IMEI = "135790246811220";
    public static final Position POS = new Position();
    public static final String MOBILEINFOSTR = "0460,0000,1877,0873,";
    public static final String GL500REPSTR = "+RESP:GTCTN,110103,135790246811220,GL500,0,0,0,25.0,81,0,0.1,0,0.3,121.390875,31.164600,20130312183936,0460,0000,1877,0873,,,,20130312190551,0304$";
    public static final String[] GL500REP = GL500REPSTR.split(QUECLINK.getFieldSeparator());

    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerDevice device = mock(TrackerDevice.class);
    private MeasurementService measurementService = mock(MeasurementService.class);
    private QueclinkLocationReport locationReport = new QueclinkLocationReport(trackerAgent, measurementService);
    private TestConnectionDetails connectionDetails = new TestConnectionDetails();

    @Before
    public void setup() throws SDKException {
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);

        POS.setAlt(new BigDecimal("0.3"));
        POS.setLng(new BigDecimal("121.390875"));
        POS.setLat(new BigDecimal("31.164600"));
    }

    @Test
    public void gl500Report() throws SDKException {
        String imei = locationReport.parse(GL500REP);
        connectionDetails.setImei(imei);
        locationReport.onParsed(new ReportContext(connectionDetails, GL500REP));

        assertEquals(IMEI, imei);
        verify(trackerAgent).getOrCreateTrackerDevice(IMEI);

        verify(device).setPosition(POS, QueclinkReports.convertEntryToDateTime("20130312190551"));
        Mobile mobile = generateMobileInfo(MOBILEINFOSTR);
        verify(device).setMobileInfo(mobile.getMcc(), mobile.getMnc(), mobile.getLac(), mobile.getCellId());
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