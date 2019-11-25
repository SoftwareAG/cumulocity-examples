package c8y.trackeragent.protocol.coban.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.utils.message.TrackerMessage;

public class HeartbeatCobanParserTest extends CobanParserTestSupport {
    
    private HeartbeatCobanParser cobanParser;

    @Before
    public void init() {
        cobanParser = new HeartbeatCobanParser(trackerAgent, serverMessages);
        connectionDetails.setImei("ABCD");
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
      String[] report = deviceMessages.heartbeat("ABCD").asArray();
      ReportContext reportCtx = new ReportContext(connectionDetails, report);
      
      boolean success = cobanParser.onParsed(reportCtx);
      
      assertThat(success).isTrue();
      assertOut("ON;");
  }

}
