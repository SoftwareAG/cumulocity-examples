package c8y.trackeragent.protocol.coban.parser;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import c8y.MotionTracking;
import c8y.Position;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.operations.OperationContext;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.message.TrackerMessage;

import com.cumulocity.rest.representation.operation.OperationRepresentation;

public class PositionUpdateCobanParserTest extends CobanParserTestSupport {

    private PositionUpdateCobanParser cobanParser;

    @Before
    public void init() {
        cobanParser = new PositionUpdateCobanParser(trackerAgent, serverMessages);
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
        TrackerMessage deviceMessage = deviceMessages.positionUpdate("ABCD", Positions.SAMPLE_1);
        when(trackerAgent.getOrCreateTrackerDevice("ABCD")).thenReturn(deviceMock);
        ReportContext reportCtx = new ReportContext(deviceMessage.asArray(), "ABCD", null);
        ArgumentCaptor<Position> positionCaptor = ArgumentCaptor.forClass(Position.class);

        boolean success = cobanParser.onParsed(reportCtx);

        verify(deviceMock).setPosition(positionCaptor.capture());
        assertThat(success).isTrue();
        assertThat(positionCaptor.getValue()).isEqualTo(Positions.SAMPLE_1);
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
        
        assertThat(operation.get("sent")).isEqualTo("**,imei:12345,101,30m;");
        assertThat(msg).isEqualTo("**,imei:12345,101,30m;");
    }

}
