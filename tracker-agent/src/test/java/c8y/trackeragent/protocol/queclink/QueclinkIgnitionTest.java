/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.queclink;

import static org.mockito.Mockito.*;

import org.joda.time.DateTime;

import static c8y.trackeragent.protocol.TrackingProtocol.QUECLINK;

import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.parser.QueclinkIgnition;
import c8y.trackeragent.server.TestConnectionDetails;
import c8y.trackeragent.utils.QueclinkReports;

public class QueclinkIgnitionTest {

    TrackerAgent trackerAgent = mock(TrackerAgent.class);
    QueclinkIgnition quecklinkIgnition = new QueclinkIgnition(trackerAgent);
    TestConnectionDetails testConnectionDetails = new TestConnectionDetails();
    TrackerDevice device = mock(TrackerDevice.class);
    
    @Before
    public void setup() {
        when(trackerAgent.getOrCreateTrackerDevice(nullable(String.class))).thenReturn(device);
    }
    
    @Test
    public void shouldCreateIgnitionOnEvent() {
        String ignitionOnReport = "+RESP:GTIGN,3C0101,135790246811220,,1200,0,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00, 12345:12:34,2000.0,20090214093254,11F0$";
        ReportContext reportCtx = new ReportContext(testConnectionDetails, ignitionOnReport.split(QUECLINK.getFieldSeparator()));
        DateTime reportDate = QueclinkReports.convertEntryToDateTime("20090214093254");
        
        quecklinkIgnition.onParsed(reportCtx);
        
        verify(device).ignitionOnEvent(reportDate);
        verify(device, never()).ignitionOffEvent(reportDate);
    }
    
    @Test
    public void shouldCreateIgnitionOffEvent() {
        String ignitionOffReport = "+RESP:GTIGF,3C0101,135790246811220,,1200,0,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00, 12345:12:34,2000.0,20090214093254,11F0$";
        DateTime reportDate = QueclinkReports.convertEntryToDateTime("20090214093254");
        
        quecklinkIgnition.onParsed(new ReportContext(testConnectionDetails, ignitionOffReport.split(QUECLINK.getFieldSeparator())));
        
        verify(device).ignitionOffEvent(reportDate);
        verify(device, never()).ignitionOnEvent(reportDate);
        
    }
    
}
