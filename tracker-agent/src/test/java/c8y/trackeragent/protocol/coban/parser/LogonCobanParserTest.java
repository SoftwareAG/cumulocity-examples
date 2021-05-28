package c8y.trackeragent.protocol.coban.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import c8y.trackeragent.UpdateIntervalProvider;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.protocol.coban.device.CobanDevice;
import c8y.trackeragent.utils.message.TrackerMessage;

public class LogonCobanParserTest extends CobanParserTestSupport {
    
    private LogonCobanParser cobanParser;

    @Before
    public void init() {
        cobanParser = new LogonCobanParser(trackerAgent, serverMessages);
        connectionDetails.setImei("ABCD");
    }
    
    @Test
    public void shouldParseImei() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.logon("ABCD");
        String actual = cobanParser.parse(deviceMessage.asArray());
        
        assertThat(actual).isEqualTo("ABCD");
    }
    
  @Test
  public void shouldProcessLogon() throws Exception {
      UpdateIntervalProvider updateIntervalProvider = mock(UpdateIntervalProvider.class);
      when(deviceMock.getGId()).thenReturn(GId.asGId("1001"));
      when(deviceMock.aLocationUpdateEvent()).thenReturn(new EventRepresentation());
      when(updateIntervalProvider.findUpdateInterval()).thenReturn(null);
      when(deviceMock.getUpdateIntervalProvider()).thenReturn(updateIntervalProvider);
      String[] report = deviceMessages.logon("ABCD").asArray();
      ReportContext reportCtx = new ReportContext(connectionDetails, report);
      currentCobanDeviceIs(new CobanDevice().setLocationReportInterval("03m"));
      
      boolean success = cobanParser.onParsed(reportCtx);
      
      assertThat(success).isTrue();
      assertOut("LOAD;**,imei:ABCD,C,03m;");
  }

}
