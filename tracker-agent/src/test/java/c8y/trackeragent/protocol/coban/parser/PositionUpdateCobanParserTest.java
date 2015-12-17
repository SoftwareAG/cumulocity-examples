package c8y.trackeragent.protocol.coban.parser;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import c8y.MotionTracking;
import c8y.Position;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.operations.OperationContext;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.TK10xCoordinatesTranslator;
import c8y.trackeragent.utils.message.TrackerMessage;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;

public class PositionUpdateCobanParserTest extends CobanParserTestSupport {

    private PositionUpdateCobanParser cobanParser;

    @Before
    public void init() {
        cobanParser = new PositionUpdateCobanParser(trackerAgent, serverMessages, alarmService);
    }
    @Test
    
    public void shouldAcceptCobanPositionMessage() throws Exception {
        boolean actual = cobanParser.accept(deviceMessages.positionUpdate("ABCD", Positions.ZERO).asArray());
        
        assertThat(actual).isTrue();
        
    }

    @Test
    public void shouldParseImei() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.positionUpdate("ABCD", Positions.ZERO);
        String actual = cobanParser.parse(deviceMessage.asArray());

        assertThat(actual).isEqualTo("ABCD");
    }

    @Test
    public void shouldProcessPositionUpdate() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.positionUpdate("ABCD", Positions.TK10xSample);
        when(trackerAgent.getOrCreateTrackerDevice("ABCD")).thenReturn(deviceMock);
        ReportContext reportCtx = new ReportContext(deviceMessage.asArray(), "ABCD", null);
        ArgumentCaptor<Position> positionCaptor = ArgumentCaptor.forClass(Position.class);

        boolean success = cobanParser.onParsed(reportCtx);

        verify(deviceMock).setPosition(positionCaptor.capture());
        assertThat(success).isTrue();
        assertThat(positionCaptor.getValue()).isEqualTo(TK10xCoordinatesTranslator.parse(Positions.TK10xSample));
    }

    @Test
    public void shouldTranslateOperation() throws Exception {
        MotionTracking motionTracking = new MotionTracking();
        motionTracking.setActive(true);
        motionTracking.setProperty("cobanRequest", "101,30m");
        OperationRepresentation operation = new OperationRepresentation();
        operation.set(motionTracking);
        OperationContext operationCtx = new OperationContext(operation, "12345");
        
        String msg = cobanParser.translate(operationCtx);
        
        assertThat(operation.get(CobanSupport.OPERATION_FRAGMENT_SERVER_COMMAND)).isEqualTo("**,imei:12345,101,30m;");
        assertThat(msg).isEqualTo("**,imei:12345,101,30m;");
    }
    
    @Test
    public void shouldSendAlarmIfNoGpsSignal() throws Exception {
        TrackerMessage deviceMessage = deviceMessages.positionUpdateNoGPS("ABCD");
        ReportContext reportCtx = new ReportContext(deviceMessage.asArray(), "ABCD", null);

        boolean success = cobanParser.onParsed(reportCtx);

        verify(deviceMock, never()).setPosition(Mockito.any(Position.class));
        assertThat(success).isTrue();
        verify(deviceMock).createAlarm(Mockito.any(AlarmRepresentation.class));
        
    }

}
