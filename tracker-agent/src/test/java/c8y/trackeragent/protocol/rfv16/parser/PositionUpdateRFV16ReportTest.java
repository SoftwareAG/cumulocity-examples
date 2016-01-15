package c8y.trackeragent.protocol.rfv16.parser;

import static c8y.trackeragent.protocol.rfv16.RFV16Constants.MESSAGE_TYPE_CMD;
import static c8y.trackeragent.protocol.rfv16.RFV16Constants.MESSAGE_TYPE_V1;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import c8y.Position;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.protocol.rfv16.RFV16ParserTestSupport;
import c8y.trackeragent.protocol.rfv16.device.RFV16Device;
import c8y.trackeragent.protocol.rfv16.parser.PositionUpdateRFV16Parser;
import c8y.trackeragent.protocol.rfv16.parser.RFV16Parser;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TK10xCoordinatesTranslator;
import c8y.trackeragent.utils.message.TrackerMessage;

import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;

public class PositionUpdateRFV16ReportTest extends RFV16ParserTestSupport {
    
    private static final String IMEI = "1234567890";
    
    PositionUpdateRFV16Parser parser;
    ArgumentCaptor<Position> positionCaptor = ArgumentCaptor.forClass(Position.class);
    ArgumentCaptor<MeasurementRepresentation> measurementCaptor = ArgumentCaptor.forClass(MeasurementRepresentation.class);
    
    @Before
    public void init() {
        parser = new PositionUpdateRFV16Parser(trackerAgent, serverMessages, measurementService);
        DateTimeUtils.setCurrentMillisFixed(0);
    }
    
    @Test    
    public void shouldParseImeiFromPositionMessage() throws Exception {
        String imei = parser.parse(deviceMessages.positionUpdate("DB", IMEI, MESSAGE_TYPE_V1, Positions.TK10xSample).asArray());
        
        assertThat(imei).isEqualTo(IMEI);
    }
    
    @Test
    public void shouldProcessPositionUpdateV1() throws Exception {
        doShouldProcessPositionUpdate(MESSAGE_TYPE_V1);
    }
    
    @Test
    public void shouldProcessPositionUpdateCMD() throws Exception {
        doShouldProcessPositionUpdate(MESSAGE_TYPE_CMD);
    }

    private void doShouldProcessPositionUpdate(String messageType) throws UnsupportedEncodingException {
        TrackerMessage deviceMessage = deviceMessages.positionUpdate("DB", IMEI, messageType, Positions.TK10xSample);
        when(trackerAgent.getOrCreateTrackerDevice(IMEI)).thenReturn(deviceMock);
        ReportContext reportCtx = new ReportContext(deviceMessage.asArray(), IMEI, out);
        currentDeviceIs(new RFV16Device().setLocationReportInterval("30"));

        parser.onParsed(reportCtx);

        verify(deviceMock).setPosition(positionCaptor.capture());
        assertThat(positionCaptor.getValue()).isEqualTo(TK10xCoordinatesTranslator.parse(Positions.TK10xSample));
        assertOut("*DB,1234567890,D1,010000,30,1#*DB,1234567890,SCF,010000,0,10#");
    }
    
    /**
     * The value is not use yet
     */
    @Test
    public void shouldParseDateTime() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.positionUpdate("DB", IMEI, MESSAGE_TYPE_V1, Positions.TK10xSample);
        ReportContext reportCtx = new ReportContext(deviceMessage.asArray(), IMEI, out);
        DateTime dateTime = RFV16Parser.getDateTime(reportCtx);
        
        assertThat(dateTime).isEqualTo(new DateTime(0));
    }

}
