package c8y.trackeragent.protocol.rfv16.parser;

import static org.fest.assertions.Assertions.assertThat;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.rest.representation.operation.OperationRepresentation;

import c8y.ArmAlarm;
import c8y.MeasurementRequestOperation;
import c8y.RFV16Config;
import c8y.Restart;
import c8y.SetSosNumber;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.protocol.rfv16.RFV16ParserTestSupport;

public class RFV16CommandTranslatorTest extends RFV16ParserTestSupport {

    private RFV16CommandTranslator commandTranslator;
    private RFV16Config rFV16Config = new RFV16Config();

    @Before
    public void init() {
        commandTranslator = new RFV16CommandTranslator(serverMessages, trackerAgent);
        currentDeviceConfigIs(rFV16Config);
    }

    @Test
    public void shouldTranslateSetSosNumberOperation() throws Exception {
        OperationRepresentation operation = new OperationRepresentation();
        SetSosNumber setSosNumber = new SetSosNumber("501501501");
        operation.set(setSosNumber);

        String response = commandTranslator.translate(asOperationContext(operation));

        assertThat(response).isEqualTo("*HQ,1234567890,S8," + HHMMSS + ",501501501,,#");
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

    @Test
    public void shouldTranslateArmAlarmOperation() throws Exception {
        OperationRepresentation operation = new OperationRepresentation();
        ArmAlarm armAlarm = new ArmAlarm();
        armAlarm.setProperty("vibration", true);
        armAlarm.setProperty("noise", false);
        operation.set(armAlarm);

        String response = commandTranslator.translate(asOperationContext(operation));
        
        assertThat(response).isEqualTo("*HQ,1234567890,SCF,010000,0,11#*HQ,1234567890,SCF,010000,0,02#");
    }

    @Test
    public void shouldTranslateEmptyArmAlarmOperation() throws Exception {
        OperationRepresentation operation = new OperationRepresentation();
        ArmAlarm armAlarm = new ArmAlarm();
        operation.set(armAlarm);

        String response = commandTranslator.translate(asOperationContext(operation));
        
        assertThat(response).isNull();
    }
    
    @Test
    public void shouldUpdateSosNumberInDevice() throws Exception {
        OperationRepresentation operation = new OperationRepresentation();
        SetSosNumber setSosNumber = new SetSosNumber("501501501");
        operation.set(setSosNumber);

        commandTranslator.translate(asOperationContext(operation));

        assertThat(rFV16Config.getSosNumber()).isEqualTo("501501501");
    }
    
    @Test
    public void shouldUpdateArmAlarmInDevice() throws Exception {
        OperationRepresentation operation = new OperationRepresentation();
        ArmAlarm armAlarm = new ArmAlarm();
        armAlarm.setProperty("vibration", true);
        armAlarm.setProperty("noise", false);
        operation.set(armAlarm);
        
        commandTranslator.translate(asOperationContext(operation));
        
        assertThat(rFV16Config.getDoorAlarmArm()).isNull();
        assertThat(rFV16Config.getVibrationAlarmArm()).isTrue();
        assertThat(rFV16Config.getNoiseAlarmArm()).isFalse();
        assertThat(rFV16Config.getDoorAlarmArm()).isNull();
    }

    private OperationContext asOperationContext(OperationRepresentation operation) {
        HashMap<String, Object> connectionParams = new HashMap<String, Object>();
        return new OperationContext(operation, IMEI, connectionParams);
    }

}
