package c8y.trackeragent.protocol.queclink;

import static c8y.trackeragent.protocol.TrackingProtocol.QUECLINK;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.parser.QueclinkPower;
import c8y.trackeragent.server.TestConnectionDetails;
import c8y.trackeragent.utils.QueclinkReports;

public class QueclinkPowerTest {

    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private QueclinkPower queclinkPower = new QueclinkPower(trackerAgent);
    private TestConnectionDetails connectionDetails = new TestConnectionDetails();
    private TrackerDevice device = mock(TrackerDevice.class);
    
    @Before
    public void setup() {
        when(trackerAgent.getOrCreateTrackerDevice(nullable(String.class))).thenReturn(device);
    }
    
    @Test
    public void shouldSendPowerOffAlarmOnGV75Report() {
        String powerOff = "+RESP:GTPFA,3C0101,135790246811220,,20170322095101,11F0$";
        
        queclinkPower.onParsed((new ReportContext(connectionDetails, 
                powerOff.split(QUECLINK.getFieldSeparator()))));
        
        DateTime expectedTime = QueclinkReports.convertEntryToDateTime("20170322095101");
        verify(device).powerAlarm(true, false, expectedTime);
        
        String externalPowerOff = "+RESP:GTMPF,3C0100,359464038005240,,0,0.0,341,114.3,7.655970,51.956593,20170321140953,0262,0001,165F,BE44,00,20170322095101,A748$";
        
        queclinkPower.onParsed((new ReportContext(connectionDetails, 
                externalPowerOff.split(QUECLINK.getFieldSeparator()))));
        
        verify(device).powerAlarm(true, true, expectedTime);
    }
    
    @Test
    public void shouldSendPowerOnAlarmOnGV75Report() {
        String powerOn = "+RESP:GTPNA,3C0101,135790246811220,,20170322095101,11F0$";
        
        queclinkPower.onParsed((new ReportContext(connectionDetails, 
                powerOn.split(QUECLINK.getFieldSeparator()))));
        
        DateTime expectedTime = QueclinkReports.convertEntryToDateTime("20170322095101");
        verify(device).powerAlarm(false, false, expectedTime);
        
        String externalPowerOn = "+RESP:GTMPN,3C0100,359464038005240,,0,0.0,341,114.3,7.655970,51.956593,20170321140953,0262,0001,165F,BE44,00,20170322095101,A748$";
        
        queclinkPower.onParsed((new ReportContext(connectionDetails, 
                externalPowerOn.split(QUECLINK.getFieldSeparator()))));
        
        verify(device).powerAlarm(false, true, expectedTime);
    }

}
