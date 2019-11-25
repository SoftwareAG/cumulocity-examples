package c8y.trackeragent.protocol.coban.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.event.EventRepresentation;

import c8y.SpeedMeasurement;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.UpdateIntervalProvider;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.coban.CobanDeviceMessages;
import c8y.trackeragent.protocol.coban.device.CobanDevice;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;
import c8y.trackeragent.server.TestConnectionDetails;
import c8y.trackeragent.service.AlarmService;
import c8y.trackeragent.service.MeasurementService;

public abstract class CobanParserTestSupport {
    
    protected TrackerAgent trackerAgent;
    protected TrackerDevice deviceMock;
    protected CobanServerMessages serverMessages = new CobanServerMessages();
    protected CobanDeviceMessages deviceMessages = new CobanDeviceMessages();
    protected AlarmService alarmService = Mockito.mock(AlarmService.class);
    protected MeasurementService measurementService = Mockito.mock(MeasurementService.class);
    protected TestConnectionDetails connectionDetails = new TestConnectionDetails();
    private UpdateIntervalProvider updateIntervalProvider = mock(UpdateIntervalProvider.class);
    
    @Before
    public void baseInit() {
        trackerAgent = mock(TrackerAgent.class);
        deviceMock = mock(TrackerDevice.class);
        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(deviceMock);
        when(deviceMock.getGId()).thenReturn(GId.asGId("1001"));
        when(deviceMock.aLocationUpdateEvent()).thenReturn(new EventRepresentation());
        when(deviceMock.getUpdateIntervalProvider()).thenReturn(updateIntervalProvider);
        when(updateIntervalProvider.findUpdateInterval()).thenReturn(null);
    }
    
    
    protected void currentCobanDeviceIs(CobanDevice cobanDevice) {
        when(deviceMock.getCobanDevice()).thenReturn(cobanDevice);
    }
    
    protected void assertOut(String expected) throws UnsupportedEncodingException {
        assertThat(connectionDetails.getOut()).isEqualTo(expected);
    }
    
    public static class CreateSpeedMeasurementAnswer implements Answer<SpeedMeasurement> {

        @Override
        public SpeedMeasurement answer(InvocationOnMock invocation) throws Throwable {
            BigDecimal speed = (BigDecimal) invocation.getArguments()[0];
            SpeedMeasurement speedMeasurement = new SpeedMeasurement();
            MeasurementValue value = new MeasurementValue(speed, "km/h", null, null, null);
            speedMeasurement.setSpeed(value);
            return speedMeasurement;
        }
        
    };
}
