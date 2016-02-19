package c8y.trackeragent.protocol.rfv16.parser;

import static c8y.trackeragent.protocol.rfv16.RFV16Constants.DEVICE_PARAM_OPERATION_IN_EXECUTION;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.svenson.AbstractDynamicProperties;

import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.ArmAlarm;
import c8y.MeasurementRequestOperation;
import c8y.RFV16Config;
import c8y.Restart;
import c8y.SetSosNumber;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.Translator;
import c8y.trackeragent.context.ConnectionContext;
import c8y.trackeragent.context.DeviceContext;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.protocol.rfv16.message.RFV16ServerMessages;
import c8y.trackeragent.utils.message.TrackerMessage;

@Component
public class RFV16CommandTranslator extends RFV16Parser implements Translator {

    private static final String LOCATION_REQUEST = "location";
    private static final String SITUATION_REQUEST = "situation";

    private static final Logger logger = LoggerFactory.getLogger(RFV16Parser.class);

    @Autowired
    public RFV16CommandTranslator(RFV16ServerMessages serverMessages, TrackerAgent trackerAgent) {
        super(trackerAgent, serverMessages);
    }
    
    @Override
    public String translate(OperationContext operationCtx) {
        
        logger.info("Handled operation {}.", operationCtx);
        if (operationCtx.getOperation().get(Restart.class) != null) {
            registerOperationAsExecuting(operationCtx);
            return translateRestart(operationCtx);
        }
        MeasurementRequestOperation request = operationCtx.getOperation().get(MeasurementRequestOperation.class);
        if (request != null && SITUATION_REQUEST.equals(request.getRequestName())) {
            registerOperationAsExecuting(operationCtx);
            return translateSituationRequest(operationCtx, request);
        }
        if (request != null && LOCATION_REQUEST.equals(request.getRequestName())) {
            registerOperationAsExecuting(operationCtx);
            return translateSingleLocationRequest(operationCtx, request);
        }
        SetSosNumber setSosNumber = operationCtx.getOperation().get(SetSosNumber.class);
        if (setSosNumber != null) {
            registerOperationAsExecuting(operationCtx);
            return translateSetSosNumber(operationCtx, setSosNumber);
        }
        ArmAlarm armAlarm = operationCtx.getOperation().get(ArmAlarm.class);
        if (armAlarm != null) {
            registerOperationAsExecuting(operationCtx);
            return translateArmAlarm(operationCtx, armAlarm);
        }
        logger.warn("Operation {} cant be translated by RFV16 protocol implementation.", operationCtx);
        return null;
    }

    private String translateRestart(OperationContext operationCtx) {
        return serverMessages.restartCommand(operationCtx.getImei()).asText();
    }

    private String translateSingleLocationRequest(OperationContext operationCtx, MeasurementRequestOperation request) {
        Integer delayInSceonds = (Integer) request.getProperty("delay");
        if (delayInSceonds == null) {
            delayInSceonds = 180;
        }
        return serverMessages.singleLocationCommand(operationCtx.getImei(), delayInSceonds.toString()).asText();
    }

    private String translateSituationRequest(OperationContext operationCtx, MeasurementRequestOperation request) {
        return serverMessages.situationCommand(operationCtx.getImei()).asText();
    }

    private String translateSetSosNumber(OperationContext operationCtx, SetSosNumber setSosNumber) {
        String msg = serverMessages.setSosNumberCommand(operationCtx.getImei(), setSosNumber.getPhoneNumber()).asText();
        TrackerDevice device = getDevice(operationCtx);
        RFV16Config deviceConfig = device.getRFV16Config();
        deviceConfig.setSosNumber(setSosNumber.getPhoneNumber());
        device.set(deviceConfig);
        return msg;
    }

    private String translateArmAlarm(OperationContext operationCtx, ArmAlarm armAlarm) {
        ArmAlarmWrapper armAlarmWrapper = new ArmAlarmWrapper(armAlarm);
        TrackerMessage trackerMessage = serverMessages.msg();
        for (String val : armAlarmWrapper.getAllFlags()) {
            TrackerMessage command = serverMessages.armAlarm(operationCtx.getImei(), val);
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
            operation = findMatchingOperation(reportCtx, Restart.class);
            if (operation != null) {
                device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
                device.setOperationSuccessful(operation);
            }
            
            operation = findMatchingRequestOperation(reportCtx, LOCATION_REQUEST);
            if (operation != null) {
                device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
                device.setOperationSuccessful(operation);
            }
            return true;
        }
        
        if (isSosConfirmation(reportCtx)) {
            operation = findMatchingOperation(reportCtx, SetSosNumber.class);
            if (operation != null) {
                device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
                device.setOperationSuccessful(operation);
            }
            return true;
        }
        
        if (isDeviceSituationConfirmation(reportCtx)) {
            operation = findMatchingRequestOperation(reportCtx, SITUATION_REQUEST);
            if (operation != null) {
                device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
                device.setOperationSuccessful(operation);
            }
            return true;
        }
        
        if (isAlarmSettingConfirmation(reportCtx)) {
            operation = findMatchingOperation(reportCtx, ArmAlarm.class);
            if (operation != null) {
                device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
                device.setOperationSuccessful(operation);
            }
            return true;
        }
        
        return false;
    }
    
    @SuppressWarnings("unchecked")
    private List<OperationRepresentation> getOperationsInExceution(ConnectionContext ctx) {
        synchronized (ctx.getImei()) {
            DeviceContext deviceContext = ctx.getDeviceContext();
            List<OperationRepresentation> operationsInExceution = (List<OperationRepresentation>) deviceContext.get(DEVICE_PARAM_OPERATION_IN_EXECUTION);
            if (operationsInExceution == null) {
                operationsInExceution = new ArrayList<OperationRepresentation>();
                deviceContext.put(DEVICE_PARAM_OPERATION_IN_EXECUTION, operationsInExceution);
            }
            return operationsInExceution;
        }
    }
    
    private void registerOperationAsExecuting(OperationContext op) {
        getOperationsInExceution(op).add(op.getOperation());
    }
    
    private <T extends AbstractDynamicProperties> OperationRepresentation findMatchingOperation(ConnectionContext connectionCtx, Class<T> operationFragment) {
        List<OperationRepresentation> operationsInExecution = getOperationsInExceution(connectionCtx);
        synchronized (connectionCtx.getImei()) {
            for (OperationRepresentation operation : operationsInExecution) {
                if (operation.get(operationFragment) != null) {
                    operationsInExecution.remove(operation);
                    return operation;
                }
            }
        }
        return null;
    }
    
    private OperationRepresentation findMatchingRequestOperation(ConnectionContext connectionCtx, String request) {
        List<OperationRepresentation> operationsInExecution = getOperationsInExceution(connectionCtx);
        synchronized (connectionCtx.getImei()) {
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
