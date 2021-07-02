/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;

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

public class GV500LocationReportTest {
    
    public static final String IMEI = "135790246811220";
    public static final Position POS = new Position();
    public static final String MOBILEINFOSTR = "0460,0000,18d8,6141,00";
    public static final String GV500REPSTR = "+RESP:GTTOW,1F0101,135790246811220,1G1JC5444R7252367,,,10,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$";
    public static final String[] GV500REP = GV500REPSTR.split(QUECLINK.getFieldSeparator());

    public static final String GV500FRISTR = "+RESP:GTFRI,1F0104,864251020004036,,,,10,1,0,,,,0,0,,0460,0000,18d8,6141,00,0.0,,,,77,420000,,,,20110101180334,001B";
    public static final String[] GV500FRI = GV500FRISTR.split(QUECLINK.getFieldSeparator());
    public static final String IMEI2 = "864251020004036";

    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerDevice device = mock(TrackerDevice.class);
    private QueclinkIgnition queclinkIgnition = mock(QueclinkIgnition.class);
    private MeasurementService measurementService = Mockito.mock(MeasurementService.class);
    private QueclinkLocationReport locationReport = new QueclinkLocationReport(trackerAgent, measurementService, queclinkIgnition);
    private TestConnectionDetails connectionDetails = new TestConnectionDetails();

    @Before
    public void setup() throws SDKException {
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);

        POS.setAlt(new BigDecimal("70.0"));
        POS.setLng(new BigDecimal("121.354335"));
        POS.setLat(new BigDecimal("31.222073"));
    }

    @Test
    public void gl500Report() throws SDKException {
        String imei = locationReport.parse(GV500REP);
        connectionDetails.setImei(imei);
        locationReport.onParsed(new ReportContext(connectionDetails, GV500REP));

        assertEquals(IMEI, imei);
        verify(trackerAgent, times(2)).getOrCreateTrackerDevice(IMEI);

        verify(device).setPosition(POS, QueclinkReports.convertEntryToDateTime("20090214093254"));
        Mobile mobile = generateMobileInfo(MOBILEINFOSTR);
        verify(device).setMobileInfo(mobile.getMcc(), mobile.getMnc(), mobile.getLac(), mobile.getCellId());
    }

    @Test
    public void gl500FRI() throws SDKException {
        String imei = locationReport.parse(GV500FRI);
        connectionDetails.setImei(imei);
        locationReport.onParsed(new ReportContext(connectionDetails, GV500FRI));

        assertEquals(IMEI2, imei);
        verify(trackerAgent).getOrCreateTrackerDevice(IMEI2);

        verify(device, never()).setPosition(POS);
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