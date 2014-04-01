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

public class TelicLocationReportTest {
    
    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerDevice device = mock(TrackerDevice.class);
    private TelicLocationReport telic = new TelicLocationReport(trackerAgent);

    @Before
    public void setup() {
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);
    }

    private void verifyReport() {
        verify(trackerAgent).getOrCreateTrackerDevice(Devices.IMEI_1);
        verify(device).setPosition(Positions.SAMPLE_1);
    }

    @Test
    public void report() {
        String[] report = Reports.getTelicReport(Devices.IMEI_1, Positions.SAMPLE_1);
        String imei = telic.parse(report);
        telic.onParsed(report, imei);
        assertEquals(Devices.IMEI_1, imei);
        verifyReport();
    }

    @Test
    public void run() throws IOException {
        Socket client = mock(Socket.class);

        byte[] bytes = Reports.getTelicReportBytes(Devices.IMEI_1, Positions.SAMPLE_1);

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ConnectedTelicTracker tracker = new ConnectedTelicTracker(client, bis, trackerAgent);
        tracker.run();
        verifyReport();
    }
}
