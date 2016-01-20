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

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.rfv16.device.RFV16Device;
import c8y.trackeragent.protocol.rfv16.message.RFV16DeviceMessages;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.service.AlarmService;
import c8y.trackeragent.service.MeasurementService;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;

public class RFV16ParserTestSupport {
    
    protected static final String IMEI = "1234567890";
    
    protected TrackerAgent trackerAgent;
    protected TrackerDevice deviceMock;
    protected RFV16ServerMessages serverMessages = new RFV16ServerMessages();
    protected RFV16DeviceMessages deviceMessages = new RFV16DeviceMessages();
    protected MeasurementService measurementService = Mockito.mock(MeasurementService.class);
    protected AlarmService alarmService = Mockito.mock(AlarmService.class);
    protected ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    @Before
    public void baseInit() {
        DateTimeUtils.setCurrentMillisFixed(0);
        trackerAgent = mock(TrackerAgent.class);
        deviceMock = mock(TrackerDevice.class);
        when(trackerAgent.getOrCreateTrackerDevice(Mockito.anyString())).thenReturn(deviceMock);
        when(deviceMock.getGId()).thenReturn(GId.asGId("1001"));
        when(deviceMock.aLocationUpdateEvent()).thenReturn(new EventRepresentation());
    }
    
    @After
    public void baseDestroy() throws IOException {
        out.close();
    }
    
    protected void currentDeviceIs(RFV16Device rFV16Device) {
        when(deviceMock.getRFV16Device()).thenReturn(rFV16Device);
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
