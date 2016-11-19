package c8y.trackeragent.operations;

import static com.cumulocity.rest.representation.operation.Operations.asExecutingOperation;
import static com.cumulocity.rest.representation.operation.Operations.asFailedOperation;
import static com.cumulocity.rest.representation.operation.Operations.asSuccessOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.server.ActiveConnection;
import c8y.trackeragent.server.ConnectionDetails;
import c8y.trackeragent.server.ConnectionsContainer;
import c8y.trackeragent.tracker.ConnectedTracker;

@Component
public class OperationExecutor {

    private static Logger logger = LoggerFactory.getLogger(OperationExecutor.class);

    private final DeviceControlApi deviceControlApi;
    private final LoggingService loggingService;
    private final IdentityRepository identityRepository;
    private final ConnectionsContainer connectionsContainer;
    private final OperationSmsDelivery operationSmsDelivery;

    @Autowired
    public OperationExecutor(DeviceControlApi deviceControlApi, LoggingService loggingService, IdentityRepository identityRepository,
            ConnectionsContainer connectionsContainer, OperationSmsDelivery operationSmsDelivery) {
        this.deviceControlApi = deviceControlApi;
        this.loggingService = loggingService;
        this.identityRepository = identityRepository;
        this.connectionsContainer = connectionsContainer;
        this.operationSmsDelivery = operationSmsDelivery;
    }

    public void execute(OperationRepresentation operation, TrackerDevice device) {
        logger.info("[OperationId : {}] " + "pending", operation.getId());
        LogfileRequest logfileRequest = operation.get(LogfileRequest.class);
        if (logfileRequest != null) {
            logger.info("[OperationId : {}] " + "Found AgentLogReques fragment", operation.getId());
            handleLogfileRequestOperation(operation, device, logfileRequest);
            return;
        }
        boolean isSmsMode = operationSmsDelivery.isSmsMode(operation, device);
        ActiveConnection connection = connectionsContainer.get(device.getImei());
        
        OperationRepresentation result;
        
        if (isSmsMode) {
            result = executeWithSMS(operation, connection, device);
        } else {
            if (connection == null) {
                markOperationFailed(operation.getId(), "Tracker device is currently not connected, cannot send operation.");
                return;
            }
            // Device is currently connected, execute on device
            result = execute(operation, connection);
        }
        

        if (OperationStatus.FAILED.toString().equals(result.getStatus())) {
            connectionsContainer.remove(device.getImei());
        }
    }

    private void handleLogfileRequestOperation(OperationRepresentation operation, TrackerDevice device, LogfileRequest logfileRequest) {
        String user = logfileRequest.getDeviceUser();
        if (StringUtils.isEmpty(user)) {
            ManagedObjectRepresentation deviceObj = device.getManagedObject();
            logfileRequest.setDeviceUser(deviceObj.getOwner());
            operation.set(logfileRequest, LogfileRequest.class);
        }
        loggingService.readLog(operation);
    }
    
    private OperationRepresentation execute(OperationRepresentation operation, ActiveConnection connection) throws SDKException {
        OperationContext operationContext = new OperationContext(connection.getConnectionDetails(), operation);
        markOperationExecuting(operation.getId());
        try {
            connection.getConnectedTracker().executeOperation(operationContext);
        } catch (Exception ex) {
            return markOperationFailed(operation.getId(), ex.getMessage());
        }
        return markOperationSuccess(operation.getId());
    }
    
    private OperationRepresentation executeWithSMS(OperationRepresentation operation, ActiveConnection connection, TrackerDevice device) {
        
        OperationContext operationContext;
        ConnectedTracker baseTracker;
        if (connection == null) {
            // if connection is null, set the tracking protocol            
            final TrackingProtocol trackingProtocol = device.getTrackingProtocolInfo();
            baseTracker = operationSmsDelivery.getTrackerForTrackingProtocol(trackingProtocol);
            
            ConnectionDetails connectionDetails = new ConnectionDetails(trackingProtocol, null, null);
            connectionDetails.setImei(device.getImei());
            operationContext = new OperationContext(connectionDetails, operation);
            
        } else {
            baseTracker = connection.getConnectedTracker();
            operationContext = new OperationContext(connection.getConnectionDetails(), operation);
        }
        
        markOperationExecuting(operation.getId());
        try {
            String translation = baseTracker.translateOperation(operationContext);
            operationSmsDelivery.deliverSms(translation, operation.getDeviceId(), device.getImei());
        } catch (Exception ex) {
            return markOperationFailed(operation.getId(), ex.getMessage());
        }
        return markOperationSuccess(operation.getId());
    }

    public void markOldExecutingOperationsFailed() throws SDKException {
        logger.debug("Cancel hanging operations (mark failed)");
        try {
            for (OperationRepresentation operation : getOperationsByStatusAndAgent(OperationStatus.EXECUTING)) {
                markOperationFailed(operation.getId(), "Operation too old!");
            }
        } catch (Exception e) {
            logger.error("Error while finishing executing operations", e);
        }
    }

    public Iterable<OperationRepresentation> getOperationsByStatusAndAgent(OperationStatus status) throws SDKException {
        GId agentId = identityRepository.find(TrackerDevice.getAgentExternalId());
        OperationFilter filter = new OperationFilter().byStatus(status).byAgent(agentId.getValue());
        return deviceControlApi.getOperationsByFilter(filter).get().allPages();
    }
    
    private OperationRepresentation markOperationFailed(GId operationId, String failureReason) {
        logger.warn("[OperationId : {}] " + failureReason, operationId);
        return deviceControlApi.update(asFailedOperation(operationId, failureReason));
    }
    
    private OperationRepresentation markOperationSuccess(GId operationId) {
        logger.info("[OperationId : {}] " + "success", operationId);
        return deviceControlApi.update(asSuccessOperation(operationId));
    }
    
    private OperationRepresentation markOperationExecuting(GId operationId) {
        logger.info("[OperationId : {}] " + "executing", operationId);
        return deviceControlApi.update(asExecutingOperation(operationId));
    }

}
