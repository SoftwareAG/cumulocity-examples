package c8y.trackeragent.protocol.coban;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import c8y.Position;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.utils.DeviceMessage;
import c8y.trackeragent.utils.Positions;

public class CobanParserTest {
    
    private TrackerAgent trackerAgent;
    private CobanParser cobanParser;
    private TrackerDevice deviceMock;
    
    @Before
    public void init() {
        trackerAgent = mock(TrackerAgent.class);
        deviceMock = mock(TrackerDevice.class);
        cobanParser = new CobanParser(trackerAgent);
    }
    
    @Test
    public void shouldParseImei() throws Exception {
        DeviceMessage deviceMessage = CobanDeviceMessages.positionUpdate("ABCD", Positions.SAMPLE_1);
        String actual = cobanParser.parse(deviceMessage.asArray());
        
        assertThat(actual).isEqualTo("ABCD");
    }
    
    @Test
    public void shouldProcessHeartbeat() throws Exception {
        when(trackerAgent.getOrCreateTrackerDevice("ABCD")).thenReturn(deviceMock);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String[] report = CobanDeviceMessages.heartbeat("ABCD").asArray();
        ReportContext reportCtx = new ReportContext(report, "ABCD", out);
        
        boolean success = cobanParser.onParsed(reportCtx);
        
        assertThat(success).isTrue();
        assertThat(out.toString("US-ASCII")).isEqualTo("ON");
    }
    
    @Test
    public void shouldProcessLogon() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String[] report = CobanDeviceMessages.logon("ABCD").asArray();
        ReportContext reportCtx = new ReportContext(report, "ABCD", out);
        
        boolean success = cobanParser.onParsed(reportCtx);
        
        assertThat(success).isTrue();
        assertThat(out.toString("US-ASCII")).isEqualTo("LOAD");
    }
    
    @Test
    public void shouldProcessPositionUpdate() throws Exception {
        DeviceMessage deviceMessage = CobanDeviceMessages.positionUpdate("ABCD", Positions.SAMPLE_1);
        when(trackerAgent.getOrCreateTrackerDevice("ABCD")).thenReturn(deviceMock);
        ReportContext reportCtx = new ReportContext(deviceMessage.asArray(), "ABCD", null);
        ArgumentCaptor<Position> positionCaptor = ArgumentCaptor.forClass(Position.class);
        
        boolean success = cobanParser.onParsed(reportCtx);
        
        verify(deviceMock).setPosition(positionCaptor.capture());
        assertThat(success).isTrue();
        assertThat(positionCaptor.getValue()).isEqualTo(Positions.SAMPLE_1);
    }

}
