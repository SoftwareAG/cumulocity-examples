package c8y.trackeragent.protocol.rfv16;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import c8y.Position;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.protocol.rfv16.device.RFV16Device;
import c8y.trackeragent.protocol.rfv16.parser.PositionUpdateRFV16Parser;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TK10xCoordinatesTranslator;
import c8y.trackeragent.utils.message.TrackerMessage;

public class PositionUpdateRFV16ReportTest extends RFV16ParserTestSupport {
    
    private static final String IMEI = "1234567890";
    
    PositionUpdateRFV16Parser parser;
    
    @Before
    public void init() {
        parser = new PositionUpdateRFV16Parser(trackerAgent, serverMessages, measurementService);
        DateTimeUtils.setCurrentMillisFixed(0);
    }
    
    @Test    
    public void shouldParseImeiFromPositionMessage() throws Exception {
        String imei = parser.parse(deviceMessages.positionUpdate("DB", IMEI, Positions.TK10xSample).asArray());
        
        assertThat(imei).isEqualTo(IMEI);
    }
    
    @Test
    public void shouldProcessPositionUpdate() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.positionUpdate("DB", IMEI, Positions.TK10xSample);
        System.out.println(deviceMessage);
        when(trackerAgent.getOrCreateTrackerDevice(IMEI)).thenReturn(deviceMock);
        ReportContext reportCtx = new ReportContext(deviceMessage.asArray(), IMEI, out);
        ArgumentCaptor<Position> positionCaptor = ArgumentCaptor.forClass(Position.class);
        currentDeviceIs(new RFV16Device().setLocationReportInterval("30"));

        parser.onParsed(reportCtx);

        verify(deviceMock).setPosition(positionCaptor.capture());
        assertThat(positionCaptor.getValue()).isEqualTo(TK10xCoordinatesTranslator.parse(Positions.TK10xSample));
        assertOut("*DB,1234567890,D1,010000,30,1#");
    }

}
