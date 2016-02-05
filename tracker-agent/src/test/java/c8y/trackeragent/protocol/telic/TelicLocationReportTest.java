package c8y.trackeragent.protocol.telic;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.net.Socket;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.context.TrackerContext;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.protocol.telic.parser.TelicFragment;
import c8y.trackeragent.protocol.telic.parser.TelicLocationReport;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TelicReports;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.context.DeviceContextServiceImpl;

public class TelicLocationReportTest {
    
    private DeviceCredentials credentials = new DeviceCredentials(null, null, null, null, null);
    private DeviceContextService contextService = new DeviceContextServiceImpl();
    private TrackerAgent trackerAgent = mock(TrackerAgent.class);
    private TrackerContext trackerContext = mock(TrackerContext.class);
    private TrackerDevice device = mock(TrackerDevice.class);
    private TelicLocationReport telic = new TelicLocationReport(trackerAgent);

    @Before
    public void setup() throws Exception {
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);
        when(trackerAgent.getContext()).thenReturn(trackerContext);
    }

    private void verifyReport() {
        verify(trackerAgent, Mockito.timeout(0)).getOrCreateTrackerDevice(Devices.IMEI_1);
        verify(device, Mockito.timeout(0)).setPosition(Positions.SAMPLE_1);
    }

    @Test
    public void testParseReport() throws Exception {
        String[] report = TelicReports.getTelicReport(Devices.IMEI_1, Positions.SAMPLE_1);
        
        String imei = telic.parse(report);
        telic.onParsed(new ReportContext(report, imei, null));
        
        assertEquals(Devices.IMEI_1, imei);
        verifyReport();
    }

    @Test
    public void testProcessReport() throws Exception {
        when(trackerContext.getDeviceCredentials(anyString())).thenReturn(credentials);
        Socket client = mock(Socket.class);
        byte[] bytes = TelicReports.getTelicReportBytes(Devices.IMEI_1, Positions.SAMPLE_1);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ConnectedTelicTracker tracker = new ConnectedTelicTracker(
        	trackerAgent, contextService, Collections.<TelicFragment>singletonList(telic));
        tracker.init(client, in);
        when(trackerContext.isDeviceRegistered(Devices.IMEI_1)).thenReturn(true);
        
        tracker.run();
        
        verifyReport();
    }
}
