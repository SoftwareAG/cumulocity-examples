package c8y.trackeragent.protocol.rfv16;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;

import c8y.RFV16Config;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.rfv16.message.RFV16DeviceMessages;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.service.AlarmService;
import c8y.trackeragent.service.MeasurementService;

public class RFV16ParserTestSupport {
    
    protected static final String IMEI = "1234567890";
    
    protected TrackerAgent trackerAgent  = mock(TrackerAgent.class);
    protected TrackerDevice deviceMock = mock(TrackerDevice.class); 
    protected RFV16ServerMessages serverMessages = new RFV16ServerMessages();
    protected RFV16DeviceMessages deviceMessages = new RFV16DeviceMessages();
    protected MeasurementService measurementService = Mockito.mock(MeasurementService.class);
    protected AlarmService alarmService = Mockito.mock(AlarmService.class);
    protected ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    @Before
    public void baseInit() {
        DateTimeUtils.setCurrentMillisFixed(0);
        when(trackerAgent.getOrCreateTrackerDevice(Mockito.anyString())).thenReturn(deviceMock);
        when(deviceMock.getGId()).thenReturn(GId.asGId("1001"));
        when(deviceMock.aLocationUpdateEvent()).thenReturn(new EventRepresentation());
    }
    
    @After
    public void baseDestroy() throws IOException {
        out.close();
    }
    
    protected void currentDeviceConfigIs(RFV16Config rFV16Config) {
        when(deviceMock.getRFV16Config()).thenReturn(rFV16Config);
    }
    
    protected void assertOut(String expected) throws UnsupportedEncodingException {
        assertThat(outAsString()).isEqualTo(expected);
    }
    
    protected void assertNothingOut() throws UnsupportedEncodingException {
        assertThat(outAsString()).isEmpty();
    }

    protected String outAsString() throws UnsupportedEncodingException {
        return out.toString("US-ASCII");
    }
    
}
