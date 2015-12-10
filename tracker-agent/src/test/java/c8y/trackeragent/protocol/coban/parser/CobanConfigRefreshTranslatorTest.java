package c8y.trackeragent.protocol.coban.parser;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.operations.OperationContext;
import c8y.trackeragent.protocol.coban.device.CobanDevice;
import c8y.trackeragent.utils.message.TrackerMessage;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;

public class CobanConfigRefreshTranslatorTest extends CobanParserTestSupport {
    
    private CobanConfigRefreshTranslator bean;
    
    @Before
    public void init() {
        bean = new CobanConfigRefreshTranslator(trackerAgent, serverMessages);
    }
    
    @Test
    public void shouldHandleRefreshConfigOperation() throws Exception {
        OperationRepresentation operation = new OperationRepresentation();
        operation.set(new Object(), CobanConfigRefreshTranslator.OPERATION_MARKER);
        OperationContext operationCtx = new OperationContext(operation, "123123");
        currentCobanDeviceIs(new CobanDevice().setLocationReportInterval("30s"));
        
        String serverMessageText = bean.translate(operationCtx);
        
        TrackerMessage actual = serverMessages.msg(serverMessageText);
        TrackerMessage expected = serverMessages.timeIntervalLocationRequest("123123", "30s");
        assertThat(actual).isEqualTo(expected);
        assertThat(operation.getStatus()).isEqualTo(OperationStatus.SUCCESSFUL.toString());
        
    }

}
