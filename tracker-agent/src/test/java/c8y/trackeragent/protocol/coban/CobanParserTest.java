package c8y.trackeragent.protocol.coban;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;

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
    public void shouldParseEmail() throws Exception {
        String actual = cobanParser.parse(new String[]{"**","imei:ABCD", "sth"});
        
        assertThat(actual).isEqualTo("ABCD");
    }
    
    @Test
    public void shouldProcessHeartbeat() throws Exception {
        when(trackerAgent.getOrCreateTrackerDevice("ABCD")).thenReturn(deviceMock);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ReportContext reportCtx = new ReportContext(new String[] { "**", "imei:ABCD" }, "ABCD", out);
        
        boolean success = cobanParser.onParsed(reportCtx);
        
        assertThat(success).isTrue();
        assertThat(out.toString("US-ASCII")).isEqualTo("ON");
    }
    
    @Test
    public void shouldProcessLogon() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ReportContext reportCtx = new ReportContext(new String[] { "**", "imei:ABCD", "A" }, "ABCD", out);
        
        boolean success = cobanParser.onParsed(reportCtx);
        
        assertThat(success).isTrue();
        assertThat(out.toString("US-ASCII")).isEqualTo("LOAD");
    }

}
