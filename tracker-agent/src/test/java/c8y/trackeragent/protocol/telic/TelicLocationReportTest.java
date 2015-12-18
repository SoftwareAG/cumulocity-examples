package c8y.trackeragent.protocol.telic;

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

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.telic.ConnectedTelicTracker;
import c8y.trackeragent.protocol.telic.parser.TelicLocationReport;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TelicReports;
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
        String[] report = TelicReports.getTelicReport(Devices.IMEI_1, Positions.SAMPLE_1);
        
        String imei = telic.parse(report);
        telic.onParsed(new ReportContext(report, imei, null));
        
        assertEquals(Devices.IMEI_1, imei);
        verifyReport();
    }

    @Test
    public void testProcessReport() throws IOException {
        Socket client = mock(Socket.class);
        byte[] bytes = TelicReports.getTelicReportBytes(Devices.IMEI_1, Positions.SAMPLE_1);
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ConnectedTelicTracker tracker = new ConnectedTelicTracker(client, bis, trackerAgent);
        when(trackerContext.isDeviceRegistered(Devices.IMEI_1)).thenReturn(true);
        
        tracker.run();
        
        verifyReport();
    }
}
