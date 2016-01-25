package c8y.trackeragent.protocol.rfv16.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.ArmAlarm;
import c8y.MeasurementRequestOperation;
import c8y.Restart;
import c8y.SetSosNumber;
import c8y.trackeragent.ConnectionContext;
import c8y.trackeragent.Translator;
import c8y.trackeragent.operations.OperationContext;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.utils.message.TrackerMessage;

public class RFV16CommandTranslator implements Translator {

    private static final Logger logger = LoggerFactory.getLogger(RFV16Parser.class);

    private final RFV16ServerMessages serverMessages;

    public RFV16CommandTranslator(RFV16ServerMessages serverMessages) {
	this.serverMessages = serverMessages;
    }

    @Override
    public String translate(OperationContext operationCtx) {
	String maker = getMaker(operationCtx);
	if (maker == null) {
	    return null;
	}
	logger.info("Handled operation {}.", operationCtx);
	if (operationCtx.getOperation().get(Restart.class) != null) {
	    return translateRestart(operationCtx, maker);
	}
	MeasurementRequestOperation request = operationCtx.getOperation().get(MeasurementRequestOperation.class);
	if (request != null && "situation".equals(request.getRequestName())) {
	    return translateSituationRequest(operationCtx, maker, request);
	}
	if (request != null && "location".equals(request.getRequestName())) {
	    return translateSingleLocationRequest(operationCtx, maker, request);
	}
	SetSosNumber setSosNumber = operationCtx.getOperation().get(SetSosNumber.class);
	if (setSosNumber != null) {
	    return translateSetSosNumber(operationCtx, maker, setSosNumber);
	}
	ArmAlarm armAlarm = operationCtx.getOperation().get(ArmAlarm.class);
	if (armAlarm != null) {
	    return translateArmAlarm(operationCtx, maker, armAlarm);
	}
	logger.warn("Operation {} cant be translated by RFV16 protocol implementation.", operationCtx);
	return null;
    }

    private String translateRestart(OperationContext operationCtx, String maker) {
	return serverMessages.restartCommand(maker, operationCtx.getImei()).asText();
    }

    private String translateSingleLocationRequest(OperationContext operationCtx, String maker, MeasurementRequestOperation request) {
	Integer delayInSceonds = (Integer) request.getProperty("delay");
	if (delayInSceonds == null) {
	    delayInSceonds = 180;
	}
	return serverMessages.singleLocationCommand(maker, operationCtx.getImei(), delayInSceonds.toString()).asText();
    }
    
    private String translateSituationRequest(OperationContext operationCtx, String maker, MeasurementRequestOperation request) {
	return serverMessages.situationCommand(maker, operationCtx.getImei()).asText();
    }
    
    private String translateSetSosNumber(OperationContext operationCtx, String maker, SetSosNumber setSosNumber) {
	return serverMessages.setSosNumberCommand(maker, operationCtx.getImei(), 
		setSosNumber.getPhoneNumber1(), 
		setSosNumber.getPhoneNumber2(), 
		setSosNumber.getPhoneNumber3()).asText();
    }

    protected String getMaker(ConnectionContext connectionContext) {
	Object connectionParam = connectionContext.getConnectionParam(RFV16Constants.CONNECTION_PARAM_MAKER);
	if (connectionParam == null) {
	    logger.warn("There are no maker param in connection {}.", connectionContext);
	    return "";
	}
	return connectionParam.toString();
    }

    private String translateArmAlarm(OperationContext operationCtx, String maker, ArmAlarm armAlarm) {
	ArmAlarmWrapper armAlarmWrapper = new ArmAlarmWrapper(armAlarm);
	TrackerMessage trackerMessage = serverMessages.msg();;
	for (String val : armAlarmWrapper.getAllValues()) {
	    TrackerMessage command = serverMessages.armAlarm(maker, operationCtx.getImei(), val);
	    trackerMessage.appendReport(command);
        }
	return trackerMessage.isEmpty() ? null : trackerMessage.asText();
    }
}
