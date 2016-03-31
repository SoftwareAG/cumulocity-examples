package c8y.trackeragent.operations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.logging.LoggingService;
import com.cumulocity.agent.server.repository.IdentityRepository;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.devicecontrol.OperationFilter;

import c8y.LogfileRequest;
import c8y.trackeragent.ConnectionRegistry;
import c8y.trackeragent.Executor;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.device.TrackerDevice;

@Component
public class OperationsHelper {
	
	private static Logger logger = LoggerFactory.getLogger(OperationsHelper.class);
	
	private final DeviceControlApi deviceControlApi;
	private final LoggingService loggingService;
	private final IdentityRepository identityRepository;
	
	@Autowired
    public OperationsHelper(DeviceControlApi deviceControlApi, LoggingService loggingService, IdentityRepository identityRepository) {
		this.deviceControlApi = deviceControlApi;
		this.loggingService = loggingService;
		this.identityRepository = identityRepository;
	}

    public void executePendingOp(OperationRepresentation operation, TrackerDevice device) {
        logger.info("Received operation with ID: {}", operation.getId());
        LogfileRequest logfileRequest = operation.get(LogfileRequest.class);
        if (logfileRequest != null) {
            logger.info("Found AgentLogRequest operation");
            String user = logfileRequest.getDeviceUser();
			if (StringUtils.isEmpty(user)) {
                ManagedObjectRepresentation deviceObj = device.getManagedObject();
                logfileRequest.setDeviceUser(deviceObj.getOwner());
                operation.set(logfileRequest, LogfileRequest.class);
            }
            loggingService.readLog(operation);
        }
        Executor exec = ConnectionRegistry.instance().get(device.getImei());
        if (exec != null) {
            // Device is currently connected, execute on device
            executeOperation(exec, device.getImei(), operation);
            if (OperationStatus.FAILED.toString().equals(operation.getStatus())) {
                // Connection error, remove device
                ConnectionRegistry.instance().remove(device.getImei());
            }
        } else {
            logger.info("Ignore operation with ID {} -> device is currently not connected to agent", operation.getId());
        }    	
    }

    private void executeOperation(Executor exec, String imei, OperationRepresentation operation) throws SDKException {
        logger.info("Executing operation with ID: {}", operation.getId());
        operation.setStatus(OperationStatus.EXECUTING.toString());
        deviceControlApi.update(operation);
        OperationContext operationContext = new OperationContext(operation, imei, exec.getConnectionParams());
        
        try {
            exec.execute(operationContext);
        } catch (Exception x) {
            String msg = "Error during communication with device " + operation.getDeviceId();
            logger.warn(msg, x);
            operation.setStatus(OperationStatus.FAILED.toString());
            operation.setFailureReason(msg + x.getMessage());
        }
        deviceControlApi.update(operation);
    }
	
    public void finishExecutingOps() throws SDKException {
        logger.debug("Cancelling hanging operations");
        try {
            for (OperationRepresentation operation : getOperationsByStatusAndAgent(OperationStatus.EXECUTING)) {
            	logger.debug("Finish operation: {}", operation);
                operation.setStatus(OperationStatus.FAILED.toString());
                deviceControlApi.update(operation);
            }
        } catch (Exception e) {
            logger.error("Error while finishing executing operations", e);
        }
    }
    

    public Iterable<OperationRepresentation> getOperationsByStatusAndAgent(OperationStatus status) throws SDKException {
    	GId agentId = identityRepository.find(TrackerDevice.getAgentExternalId());
        OperationFilter opsFilter = new OperationFilter().byStatus(status).byAgent(agentId.getValue());
        return deviceControlApi.getOperationsByFilter(opsFilter).get().allPages();
    }

}
