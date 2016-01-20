package c8y.trackeragent.protocol.coban.parser;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.coban.CobanDeviceMessages;
import c8y.trackeragent.protocol.coban.device.CobanDevice;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.service.AlarmService;
import c8y.trackeragent.service.MeasurementService;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;

public abstract class CobanParserTestSupport {
    
    protected TrackerAgent trackerAgent;
    protected TrackerDevice deviceMock;
    protected CobanServerMessages serverMessages = new CobanServerMessages();
    protected CobanDeviceMessages deviceMessages = new CobanDeviceMessages();
    protected AlarmService alarmService = Mockito.mock(AlarmService.class);
    protected MeasurementService measurementService = new MeasurementService();
    protected ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    @Before
    public void baseInit() {
        trackerAgent = mock(TrackerAgent.class);
        deviceMock = mock(TrackerDevice.class);
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(deviceMock);
        when(deviceMock.getGId()).thenReturn(GId.asGId("1001"));
        when(deviceMock.aLocationUpdateEvent()).thenReturn(new EventRepresentation());
    }
    
    @After
    public void baseDestroy() throws IOException {
        out.close();
    }
    
    protected void currentCobanDeviceIs(CobanDevice cobanDevice) {
        when(deviceMock.getCobanDevice()).thenReturn(cobanDevice);
    }
    
    protected void assertOut(String expected) throws UnsupportedEncodingException {
        assertThat(out.toString("US-ASCII")).isEqualTo(expected);
    }
}
