package c8y.trackeragent.protocol.coban.parser;

import static org.mockito.Mockito.mock;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;

public abstract class CobanParserTestSupport {
    
    protected TrackerAgent trackerAgent;
    protected TrackerDevice deviceMock;
    
    public void init() {
        trackerAgent = mock(TrackerAgent.class);
        deviceMock = mock(TrackerDevice.class);
    }
}
