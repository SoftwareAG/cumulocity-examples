package c8y.trackeragent.protocol.coban.parser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

import com.cumulocity.model.idtype.GId;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.coban.CobanDeviceMessages;
import c8y.trackeragent.protocol.coban.device.CobanDevice;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.protocol.coban.service.AlarmService;

public abstract class CobanParserTestSupport {
    
    protected TrackerAgent trackerAgent;
    protected TrackerDevice deviceMock;
    protected CobanServerMessages serverMessages = new CobanServerMessages();
    protected CobanDeviceMessages deviceMessages = new CobanDeviceMessages();
    protected AlarmService alarmService = new AlarmService();
    protected ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    @Before
    public void baseInit() {
        trackerAgent = mock(TrackerAgent.class);
        deviceMock = mock(TrackerDevice.class);
        when(trackerAgent.getOrCreateTrackerDevice(Mockito.anyString())).thenReturn(deviceMock);
        when(deviceMock.getGId()).thenReturn(GId.asGId("1001"));
    }
    
    @After
    public void baseDestroy() throws IOException {
        out.close();
    }
    
    protected void currentCobanDeviceIs(CobanDevice cobanDevice) {
        when(deviceMock.getCobanDevice()).thenReturn(cobanDevice);
    }
}
