package c8y.trackeragent.protocol.coban.parser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.mockito.Mockito;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.coban.CobanDeviceMessages;
import c8y.trackeragent.protocol.coban.device.CobanDevice;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;

public abstract class CobanParserTestSupport {
    
    protected TrackerAgent trackerAgent;
    protected TrackerDevice deviceMock;
    protected CobanServerMessages serverMessages = new CobanServerMessages();
    protected CobanDeviceMessages deviceMessages = new CobanDeviceMessages();
    
    @Before
    public void baseInit() {
        trackerAgent = mock(TrackerAgent.class);
        deviceMock = mock(TrackerDevice.class);
        when(trackerAgent.getOrCreateTrackerDevice(Mockito.anyString())).thenReturn(deviceMock);
    }
    
    protected void currentCobanDeviceIs(CobanDevice cobanDevice) {
        when(deviceMock.getCobanDevice()).thenReturn(cobanDevice);
    }
}
