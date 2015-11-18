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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import c8y.Position;
import c8y.trackeragent.GL200Constants;
import c8y.trackeragent.GL200LocationReport;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;

import com.cumulocity.sdk.client.SDKException;

public class GV500LocationReportTest {
    public static final String IMEI = "135790246811220";
    public static final Position POS = new Position();
    public static final String LAC = "18d8";
    public static final String CELLID = "6141";

    public static final String GV500REPSTR = "+RESP:GTTOW,1F0101,135790246811220,1G1JC5444R7252367,,,10,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$";
    public static final String[] GV500REP = GV500REPSTR.split(GL200Constants.FIELD_SEP);

    public static final String GV500FRISTR = "+RESP:GTFRI,1F0104,864251020004036,,,,10,1,0,,,,0,0,,0262,0007,18d8,6141,00,0.0,,,,77,420000,,,,20110101180334,001B";
    public static final String[] GV500FRI = GV500FRISTR.split(GL200Constants.FIELD_SEP);
    public static final String IMEI2 = "864251020004036";

    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerDevice device = mock(TrackerDevice.class);
    private GL200LocationReport locationReport = new GL200LocationReport(trackerAgent);

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
        locationReport.onParsed(new ReportContext(GV500REP, imei, null));

        assertEquals(IMEI, imei);
        verify(trackerAgent).getOrCreateTrackerDevice(IMEI);

        verify(device).setPosition(POS);
        verify(device).setCellId(LAC + "-" + CELLID);
    }

    @Test
    public void gl500FRI() throws SDKException {
        String imei = locationReport.parse(GV500FRI);
        locationReport.onParsed(new ReportContext(GV500FRI, imei, null));

        assertEquals(IMEI2, imei);
        verify(trackerAgent).getOrCreateTrackerDevice(IMEI2);

        verify(device, never()).setPosition(POS);
        verify(device).setCellId(LAC + "-" + CELLID);
    }
}