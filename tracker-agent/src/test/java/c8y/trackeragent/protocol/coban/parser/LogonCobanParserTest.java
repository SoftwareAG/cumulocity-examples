package c8y.trackeragent.protocol.coban.parser;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.protocol.coban.device.CobanDevice;
import c8y.trackeragent.utils.message.TrackerMessage;

public class LogonCobanParserTest extends CobanParserTestSupport {
    
    private LogonCobanParser cobanParser;

    @Before
    public void init() {
        cobanParser = new LogonCobanParser(trackerAgent, serverMessages);
    }
    
    @Test
    public void shouldParseImei() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.logon("ABCD");
        String actual = cobanParser.parse(deviceMessage.asArray());
        
        assertThat(actual).isEqualTo("ABCD");
    }
    
  @Test
  public void shouldProcessLogon() throws Exception {
      String[] report = deviceMessages.logon("ABCD").asArray();
      ReportContext reportCtx = new ReportContext(report, "ABCD", out);
      currentCobanDeviceIs(new CobanDevice().setLocationReportInterval("03m"));
      
      boolean success = cobanParser.onParsed(reportCtx);
      
      assertThat(success).isTrue();
      assertOut("LOAD;**,imei:ABCD,C,03m;");
  }

}
