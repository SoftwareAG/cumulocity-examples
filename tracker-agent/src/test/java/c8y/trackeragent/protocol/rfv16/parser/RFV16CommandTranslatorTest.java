package c8y.trackeragent.protocol.rfv16.parser;

import static org.fest.assertions.Assertions.assertThat;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import c8y.MeasurementRequestOperation;
import c8y.Restart;
import c8y.SetSosNumber;
import c8y.trackeragent.operations.OperationContext;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.protocol.rfv16.RFV16ParserTestSupport;

import com.cumulocity.rest.representation.operation.OperationRepresentation;

public class RFV16CommandTranslatorTest extends RFV16ParserTestSupport {
    
    private RFV16CommandTranslator commandTranslator;

    @Before
    public void init() {
        commandTranslator = new RFV16CommandTranslator(serverMessages);
    }
 
    @Test
    public void shouldTranslateSetSosNumberOperation() throws Exception {
	OperationRepresentation operation = new OperationRepresentation();
	SetSosNumber setSosNumber = new SetSosNumber("501501501", "502502502", "503503503");
	operation.set(setSosNumber);
	
	String response = commandTranslator.translate(asOperationContext(operation));
	
	assertThat(response).isEqualTo("*HQ,1234567890,S8,010000,501501501,502502502,503503503#");
    }
    
    @Test
    public void shouldTranslateRestartOperation() throws Exception {
	OperationRepresentation operation = new OperationRepresentation();
	operation.set(new Restart());
	
	String response = commandTranslator.translate(asOperationContext(operation));
	
	assertThat(response).isEqualTo("*HQ,1234567890,R1,010000#");
    }
    
    @Test
    public void shouldTranslateSituationRequestOperation() throws Exception {
	OperationRepresentation operation = new OperationRepresentation();
	operation.set(new MeasurementRequestOperation("situation"));
	
	String response = commandTranslator.translate(asOperationContext(operation));
	
	assertThat(response).isEqualTo("*HQ,1234567890,CK,010000#");
    }
    
    @Test
    public void shouldTranslateLocationRequestOperation() throws Exception {
	OperationRepresentation operation = new OperationRepresentation();
	operation.set(new MeasurementRequestOperation("location"));
	
	String response = commandTranslator.translate(asOperationContext(operation));
	assertThat(response).isEqualTo("*HQ,1234567890,LOC,010000,180#");
	
	operation.get(MeasurementRequestOperation.class).setProperty("delay", 350);
	
	response = commandTranslator.translate(asOperationContext(operation));
	assertThat(response).isEqualTo("*HQ,1234567890,LOC,010000,350#");
    }
    
    private OperationContext asOperationContext(OperationRepresentation operation) {
	HashMap<String, Object> connectionParams = new HashMap<String, Object>();
	connectionParams.put(RFV16Constants.CONNECTION_PARAM_MAKER, "HQ");
	return new OperationContext(operation, IMEI, connectionParams);
    }

}
