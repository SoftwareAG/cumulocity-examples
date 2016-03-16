/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package c8y.trackeragent.operations;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.cumulocity.agent.server.context.DeviceContext;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.logging.LoggingService;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.OperationFilter;

import c8y.LogfileRequest;
import c8y.trackeragent.ConnectionRegistry;
import c8y.trackeragent.Executor;
import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.device.ManagedObjectCache;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;

/**
 * Polls the platform for pending operations, executes the operations and
 * reports back the status. Operations can only be executed on devices that are
 * currently connected to the agent. Operations for devices that are currently
 * not connected are left in the queue on the platform for retry.
 * 
 */
public class OperationDispatcher implements Runnable {
    
	private static Logger logger = LoggerFactory.getLogger(OperationDispatcher.class);
	
    private static final long POLLING_DELAY = 10;
    private static final long POLLING_INTERVAL = 10;
    
    private final LoggingService loggingService;
    private final DeviceContextService contextService;
    private final DeviceCredentials tenantCredentials;
    private final TrackerPlatform platform;
    private volatile ScheduledFuture<?> self;


    public OperationDispatcher(TrackerPlatform tenantPlatform, DeviceCredentials tenantCredentials, LoggingService loggingService, DeviceContextService contextService) throws SDKException {
        this.platform = tenantPlatform;
		this.tenantCredentials = tenantCredentials;
        this.loggingService = loggingService;
        this.contextService = contextService;
        finishExecutingOps();
    }

    private void finishExecutingOps() throws SDKException {
        logger.debug("Cancelling hanging operations");
        try {
            for (OperationRepresentation operation : getOperationsByStatus(OperationStatus.EXECUTING)) {
                operation.setStatus(OperationStatus.FAILED.toString());
                platform.getDeviceControlApi().update(operation);
            }
        } catch (Exception e) {
            logger.error("Error while finishing executing operations", e);
        }
    }

    @Override
    public void run() {
        logger.debug("Executing queued operations");
        try {
            contextService.enterContext(new DeviceContext(tenantCredentials));
            executePendingOps();
            contextService.leaveContext();
        } catch (Exception x) {
            logger.warn("Error while executing operations", x);
        }
    }

    private void executePendingOps() throws SDKException {
        logger.debug("Querying for pending operations");
        for (OperationRepresentation operation : getOperationsByStatus(OperationStatus.PENDING)) {
        	GId deviceId = operation.getDeviceId();
        	TrackerDevice device = ManagedObjectCache.instance().get(deviceId);
        	if (device == null) {
        		logger.debug("Ignore operation with ID {} -> device with id {} hasn't been identified yet", operation.getId(), deviceId);
        		continue; // Device hasn't been identified yet
        	}
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
    }

    private void executeOperation(Executor exec, String imei, OperationRepresentation operation) throws SDKException {
        logger.info("Executing operation with ID: {}", operation.getId());
        operation.setStatus(OperationStatus.EXECUTING.toString());
        platform.getDeviceControlApi().update(operation);
        OperationContext operationContext = new OperationContext(operation, imei, exec.getConnectionParams());
        
        try {
            exec.execute(operationContext);
        } catch (Exception x) {
            String msg = "Error during communication with device " + operation.getDeviceId();
            logger.warn(msg, x);
            operation.setStatus(OperationStatus.FAILED.toString());
            operation.setFailureReason(msg + x.getMessage());
        }
        platform.getDeviceControlApi().update(operation);
    }

    private Iterable<OperationRepresentation> getOperationsByStatus(OperationStatus status) throws SDKException {
        OperationFilter opsFilter = new OperationFilter().byStatus(status);
        Iterable<OperationRepresentation> operationsIterable = Collections.emptyList();
        try {
            operationsIterable = platform.getDeviceControlApi().getOperationsByFilter(opsFilter).get().allPages();
        } catch (SDKException e) {
            if (hasIncorrectStatus(e) && self != null) {
                self.cancel(false);
            }
        }
        return operationsIterable;
    }

    private boolean hasIncorrectStatus(SDKException e) {
        // 404 - someone deleted device, 401 tenant is disabled
        return e.getHttpStatus() == 404 || e.getHttpStatus() == 401;
    }

    public void startPolling(ScheduledExecutorService operationsExecutor) {
        self = operationsExecutor.scheduleWithFixedDelay(this, POLLING_DELAY, POLLING_INTERVAL, SECONDS);
    }
}
