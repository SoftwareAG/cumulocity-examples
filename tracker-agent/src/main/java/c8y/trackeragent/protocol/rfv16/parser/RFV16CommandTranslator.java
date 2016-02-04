package c8y.trackeragent.protocol.rfv16.parser;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.svenson.AbstractDynamicProperties;

import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.google.common.collect.Lists;

import c8y.ArmAlarm;
import c8y.MeasurementRequestOperation;
import c8y.RFV16Config;
import c8y.Restart;
import c8y.SetSosNumber;
import c8y.trackeragent.ConnectionContext;
import c8y.trackeragent.Parser;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.Translator;
import c8y.trackeragent.operations.OperationContext;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.utils.message.TrackerMessage;

@Component
public class RFV16CommandTranslator extends RFV16Parser implements Parser, Translator, RFV16Fragment {

    private static final String LOCATION_REQUEST = "location";
    private static final String SITUATION_REQUEST = "situation";

    private static final Logger logger = LoggerFactory.getLogger(RFV16Parser.class);

    private volatile List<OperationRepresentation> operationsInExecution;

    @Autowired
    public RFV16CommandTranslator(RFV16ServerMessages serverMessages, TrackerAgent trackerAgent) {
        super(trackerAgent, serverMessages);
        operationsInExecution = Lists.newArrayList();
    }
    
    @Override
    public String translate(OperationContext operationCtx) {
        String maker = getMaker(operationCtx);
        if (maker == null) {
            return null;
        }
        
        logger.info("Handled operation {}.", operationCtx);
        if (operationCtx.getOperation().get(Restart.class) != null) {
            addOperation(operationCtx.getOperation());
            return translateRestart(operationCtx, maker);
        }
        MeasurementRequestOperation request = operationCtx.getOperation().get(MeasurementRequestOperation.class);
        if (request != null && SITUATION_REQUEST.equals(request.getRequestName())) {
            addOperation(operationCtx.getOperation());
            return translateSituationRequest(operationCtx, maker, request);
        }
        if (request != null && LOCATION_REQUEST.equals(request.getRequestName())) {
            addOperation(operationCtx.getOperation());
            return translateSingleLocationRequest(operationCtx, maker, request);
        }
        SetSosNumber setSosNumber = operationCtx.getOperation().get(SetSosNumber.class);
        if (setSosNumber != null) {
            addOperation(operationCtx.getOperation());
            return translateSetSosNumber(operationCtx, maker, setSosNumber);
        }
        ArmAlarm armAlarm = operationCtx.getOperation().get(ArmAlarm.class);
        if (armAlarm != null) {
            addOperation(operationCtx.getOperation());
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
        String msg = serverMessages.setSosNumberCommand(maker, operationCtx.getImei(), setSosNumber.getPhoneNumber()).asText();
        TrackerDevice device = getDevice(operationCtx);
        RFV16Config deviceConfig = device.getRFV16Config();
        deviceConfig.setSosNumber(setSosNumber.getPhoneNumber());
        device.set(deviceConfig);
        return msg;
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
        TrackerMessage trackerMessage = serverMessages.msg();
        for (String val : armAlarmWrapper.getAllFlags()) {
            TrackerMessage command = serverMessages.armAlarm(maker, operationCtx.getImei(), val);
            trackerMessage.appendReport(command);
        }
        TrackerDevice device = getDevice(operationCtx);
        RFV16Config deviceConfig = device.getRFV16Config();
        deviceConfig.setDoorAlarmArm(armAlarmWrapper.getDoor());
        deviceConfig.setNoiseAlarmArm(armAlarmWrapper.getNoise());
        deviceConfig.setVibrationAlarmArm(armAlarmWrapper.getVibration());
        deviceConfig.setSosAlarmArm(armAlarmWrapper.getSos());
        device.set(deviceConfig);
        
        return trackerMessage.isEmpty() ? null : trackerMessage.asText();
    }
    
    public TrackerDevice getDevice(ConnectionContext connCtx) {
        return trackerAgent.getOrCreateTrackerDevice(connCtx.getImei());
    }
    
    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        OperationRepresentation operation;
        TrackerDevice device;
        
        if (isV1orNBR(reportCtx)) {
            operation = findMatchingOperation(Restart.class);
            if (operation != null) {
                device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
                device.setOperationSuccessful(operation);
            }
            
            operation = findMatchingRequestOperation(LOCATION_REQUEST);
            if (operation != null) {
                device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
                device.setOperationSuccessful(operation);
            }
            return true;
        }
        
        if (isSosConfirmation(reportCtx)) {
            operation = findMatchingOperation(SetSosNumber.class);
            if (operation != null) {
                device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
                device.setOperationSuccessful(operation);
            }
            return true;
        }
        
        if (isDeviceSituationConfirmation(reportCtx)) {
            operation = findMatchingRequestOperation(SITUATION_REQUEST);
            if (operation != null) {
                device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
                device.setOperationSuccessful(operation);
            }
            return true;
        }
        
        if (isAlarmSettingConfirmation(reportCtx)) {
            operation = findMatchingOperation(ArmAlarm.class);
            if (operation != null) {
                device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
                device.setOperationSuccessful(operation);
            }
            return true;
        }
        
        return false;
    }
    
    private void addOperation(OperationRepresentation op) {
        synchronized (operationsInExecution) {
            operationsInExecution.add(op);
        }
    }
    
    private <T extends AbstractDynamicProperties> OperationRepresentation findMatchingOperation(Class<T> operationFragment) {
        synchronized (operationsInExecution) {
            for (OperationRepresentation operation : operationsInExecution) {
                if (operation.get(operationFragment) != null) {
                    operationsInExecution.remove(operation);
                    return operation;
                }
            }
        }
        return null;
    }
    
    private OperationRepresentation findMatchingRequestOperation(String request) {
        synchronized (operationsInExecution) {
            for (OperationRepresentation operation : operationsInExecution) {
                MeasurementRequestOperation fragment = operation.get(MeasurementRequestOperation.class);
                if (fragment != null && fragment.getRequestName().equals(request)) {
                    operationsInExecution.remove(operation);
                    return operation;
                }
            }
        }
        return null;
    }
    
    private boolean isSosConfirmation(ReportContext reportCtx) {
        return RFV16Constants.MESSAGE_TYPE_V4.equals(reportCtx.getEntry(2))
                && RFV16Constants.COMMAND_SET_SOS_NUMBER.equals(reportCtx.getEntry(3));
    }
    
    private boolean isDeviceSituationConfirmation(ReportContext reportCtx) {
        return RFV16Constants.MESSAGE_TYPE_V4.equals(reportCtx.getEntry(2))
                && RFV16Constants.COMMAND_DISPLAY_DEVICE_SITUATION.equals(reportCtx.getEntry(3));
    }
    
    private boolean isAlarmSettingConfirmation(ReportContext reportCtx) {
        return RFV16Constants.MESSAGE_TYPE_V4.equals(reportCtx.getEntry(2))
                && RFV16Constants.COMMAND_ARM_DISARM_ALARM.equals(reportCtx.getEntry(3));
    }
    
    private boolean isV1orNBR(ReportContext reportCtx) {
        return RFV16Constants.MESSAGE_TYPE_V1.equals(reportCtx.getEntry(2))
                || RFV16Constants.MESSAGE_TYPE_MULTI_BASE_STATION_DATA.equals(reportCtx.getEntry(2));
    }
}
