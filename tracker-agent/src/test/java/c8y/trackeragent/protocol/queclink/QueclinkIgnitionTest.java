package c8y.trackeragent.protocol.queclink;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static c8y.trackeragent.protocol.TrackingProtocol.QUECLINK;

import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.parser.QueclinkIgnition;
import c8y.trackeragent.server.TestConnectionDetails;

public class QueclinkIgnitionTest {

    TrackerAgent trackerAgent = mock(TrackerAgent.class);
    QueclinkIgnition quecklinkIgnition = new QueclinkIgnition(trackerAgent);
    TestConnectionDetails testConnectionDetails = new TestConnectionDetails();
    TrackerDevice device = mock(TrackerDevice.class);
    
    @Before
    public void setup() {
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);
    }
    
    @Test
    public void shouldCreateIgnitionOnEvent() {
        String ignitionOnReport = "+RESP:GTIGN,3C0101,135790246811220,,1200,0,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00, 12345:12:34,2000.0,20090214093254,11F0$";
        ReportContext reportCtx = new ReportContext(testConnectionDetails, ignitionOnReport.split(QUECLINK.getFieldSeparator()));
        DateTime reportDate = convertEntryToDateTime("20090214093254");
        
        quecklinkIgnition.onParsed(reportCtx);
        
        verify(device).ignitionOnEvent(reportDate);
        verify(device, never()).ignitionOffEvent(reportDate);
    }
    
    @Test
    public void shouldCreateIgnitionOffEvent() {
        String ignitionOffReport = "+RESP:GTIGF,3C0101,135790246811220,,1200,0,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00, 12345:12:34,2000.0,20090214093254,11F0$";
        DateTime reportDate = convertEntryToDateTime("20090214093254");
        
        quecklinkIgnition.onParsed(new ReportContext(testConnectionDetails, ignitionOffReport.split(QUECLINK.getFieldSeparator())));
        
        verify(device).ignitionOffEvent(reportDate);
        verify(device, never()).ignitionOnEvent(reportDate);
        
    }
    
    private DateTime convertEntryToDateTime(String reportDate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        DateTime dateTime = formatter.parseDateTime(reportDate);
        return dateTime;
    }
    
}
