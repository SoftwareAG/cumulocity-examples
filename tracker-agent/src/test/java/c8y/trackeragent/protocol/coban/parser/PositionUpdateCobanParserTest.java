package c8y.trackeragent.protocol.coban.parser;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import c8y.Position;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.protocol.coban.CobanDeviceMessages;
import c8y.trackeragent.utils.DeviceMessage;
import c8y.trackeragent.utils.Positions;

public class PositionUpdateCobanParserTest extends CobanParserTestSupport {
    
    private PositionUpdateCobanParser cobanParser;

    @Before
    public void init() {
        super.init();
        cobanParser = new PositionUpdateCobanParser(trackerAgent);
    }
    
    @Test
    public void shouldParseImei() throws Exception {
        DeviceMessage deviceMessage = CobanDeviceMessages.positionUpdate("ABCD", Positions.ZERO);
        String actual = cobanParser.parse(deviceMessage.asArray());
        
        assertThat(actual).isEqualTo("ABCD");
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
