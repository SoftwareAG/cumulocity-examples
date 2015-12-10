package c8y.trackeragent.protocol.coban.parser;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.utils.message.TrackerMessage;

public class HeartbeatCobanParserTest extends CobanParserTestSupport {
    
    private HeartbeatCobanParser cobanParser;

    @Before
    public void init() {
        super.init();
        cobanParser = new HeartbeatCobanParser(trackerAgent, serverMessages);
    }
    
    @Test
    public void shouldParseImei() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.heartbeat("ABCD");
        String actual = cobanParser.parse(deviceMessage.asArray());
        
        assertThat(actual).isEqualTo("ABCD");
    }
    
  @Test
  public void shouldProcessHeartbeat() throws Exception {
      when(trackerAgent.getOrCreateTrackerDevice("ABCD")).thenReturn(deviceMock);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      String[] report = deviceMessages.heartbeat("ABCD").asArray();
      ReportContext reportCtx = new ReportContext(report, "ABCD", out);
      
      boolean success = cobanParser.onParsed(reportCtx);
      
      assertThat(success).isTrue();
      assertThat(out.toString("US-ASCII")).isEqualTo("ON;");
  }

}
