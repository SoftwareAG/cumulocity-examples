package c8y.trackeragent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.Reports;
import c8y.trackeragent.utils.TrackerContext;

public class TelicLocationReportTest {
    
    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerContext trackerContext = mock(TrackerContext.class);
    private TrackerDevice device = mock(TrackerDevice.class);
    private TelicLocationReport telic = new TelicLocationReport(trackerAgent);

    @Before
    public void setup() {
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);
        when(trackerAgent.getContext()).thenReturn(trackerContext);
    }

    private void verifyReport() {
        verify(trackerAgent).getOrCreateTrackerDevice(Devices.IMEI_1);
        verify(device).setPosition(Positions.SAMPLE_1);
    }

    @Test
    public void testParseReport() {
        String[] report = Reports.getTelicReport(Devices.IMEI_1, Positions.SAMPLE_1);
        
        String imei = telic.parse(report);
        telic.onParsed(report, imei);
        
        assertEquals(Devices.IMEI_1, imei);
        verifyReport();
    }

    @Test
    public void testProcessReport() throws IOException {
        Socket client = mock(Socket.class);
        byte[] bytes = Reports.getTelicReportBytes(Devices.IMEI_1, Positions.SAMPLE_1);
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ConnectedTelicTracker tracker = new ConnectedTelicTracker(client, bis, trackerAgent);
        when(trackerContext.isDeviceRegistered(Devices.IMEI_1)).thenReturn(true);
        
        tracker.run();
        
        verifyReport();
    }
}
