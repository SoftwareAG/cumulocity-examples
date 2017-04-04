package c8y.trackeragent.protocol.queclink;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import static c8y.trackeragent.protocol.TrackingProtocol.QUECLINK;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.parser.QueclinkIgnition;
import c8y.trackeragent.protocol.queclink.parser.QueclinkLocationReport;
import c8y.trackeragent.server.TestConnectionDetails;
import c8y.trackeragent.service.MeasurementService;
import c8y.trackeragent.utils.QueclinkReports;

public class QueclinkLocationReportTest {

    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerDevice device = mock(TrackerDevice.class);
    private MeasurementService measurementService = Mockito.mock(MeasurementService.class);
    
    private QueclinkIgnition queclinkIgnition = new QueclinkIgnition(trackerAgent);
    private QueclinkLocationReport locationReportParser = new QueclinkLocationReport(trackerAgent, measurementService, queclinkIgnition);
    
    private TestConnectionDetails connectionDetails = new TestConnectionDetails();
    
    private String[] locationReports = {
            "+RESP:GTTOW,3C0101,135790246811220,,,10,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$", //tow report, gv75
            "+RESP:GTNMR,1A0500,135790246811220,,0,0,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,,20090214093254,11F0$", //gl300
            "+RESP:GTFRI,02010B,135790246811220,,0,0,2,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,0,4.3,92,70.0,121.354336,31.222074,20090101000000,0460,0000,18d8,6142,00,10,20090214093254,11F0$" //gl200
    };
    
    
    @Before
    public void setup() {
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);
    }
    
    @Test
    public void shouldCreateSpeedMeasurement() {
        
        for (String locationReport: locationReports) {
            locationReportParser.onParsed(new ReportContext(connectionDetails, 
                    locationReport.split(QUECLINK.getFieldSeparator())));
        }
        
        BigDecimal expectedSpeed = new BigDecimal("4.3");
        DateTime expectedTime = QueclinkReports.convertEntryToDateTime("20090214093254");
        verify(measurementService, times(4)).createSpeedMeasurement(eq(expectedSpeed), eq(device), eq(expectedTime));
        
    }
    
    @Test
    public void shouldNotCreateSpeedEventWhenMissingOrInvalidValue() {
        String reportMissingSpeed = "+RESP:GTTOW,3C0101,135790246811220,,,10,1,1,,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$";
        locationReportParser.onParsed(new ReportContext(connectionDetails, 
                reportMissingSpeed.split(QUECLINK.getFieldSeparator())));
        
        verify(measurementService, never()).createSpeedMeasurement((BigDecimal) any(), eq(device), (DateTime) any());
        
        String reportInvalidSpeed = "+RESP:GTTOW,3C0101,135790246811220,,,10,1,1,invalid,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$";
        locationReportParser.onParsed(new ReportContext(connectionDetails, 
                reportInvalidSpeed.split(QUECLINK.getFieldSeparator())));
        
        verify(measurementService, never()).createSpeedMeasurement((BigDecimal) any(), eq(device), (DateTime) any());
    }
    
    @Test
    public void shouldCreateTowEvent() {
        locationReportParser.onParsed(new ReportContext(connectionDetails, 
                locationReports[0].split(QUECLINK.getFieldSeparator())));
        
        DateTime expectedTime = QueclinkReports.convertEntryToDateTime("20090214093254");
        verify(device).towEvent(eq(expectedTime));
    }
    
    @Test
    public void shouldCreateIgnitionEvent() {
        //ignition on
        String ignitionOnReport = "+RESP:GTIGL,3C0101,135790246811220,,,00,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$";
        locationReportParser.onParsed(new ReportContext(connectionDetails, 
                ignitionOnReport.split(QUECLINK.getFieldSeparator())));
        
        DateTime expectedTime = QueclinkReports.convertEntryToDateTime("20090214093254");
        verify(device).ignitionOnEvent(expectedTime);
        verify(device, times(0)).ignitionOffEvent(expectedTime);
        
        //ignition off
        String ignitionOffReport = "+RESP:GTIGL,3C0101,135790246811220,,,01,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$";
        locationReportParser.onParsed(new ReportContext(connectionDetails, 
                ignitionOffReport.split(QUECLINK.getFieldSeparator())));
        
        verify(device).ignitionOffEvent(expectedTime);
        verify(device, times(1)).ignitionOnEvent(expectedTime);
        
    }
}
