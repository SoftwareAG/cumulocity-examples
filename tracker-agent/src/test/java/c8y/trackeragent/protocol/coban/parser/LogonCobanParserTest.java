package c8y.trackeragent.protocol.coban.parser;

import static org.fest.assertions.Assertions.assertThat;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.protocol.coban.CobanDeviceMessages;
import c8y.trackeragent.utils.message.TrackerMessage;

public class LogonCobanParserTest extends CobanParserTestSupport {
    
    private LogonCobanParser cobanParser;

    @Before
    public void init() {
        super.init();
        cobanParser = new LogonCobanParser(trackerAgent);
    }
    
    @Test
    public void shouldParseImei() throws Exception {
        TrackerMessage deviceMessage = CobanDeviceMessages.logon("ABCD");
        String actual = cobanParser.parse(deviceMessage.asArray());
        
        assertThat(actual).isEqualTo("ABCD");
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

}
