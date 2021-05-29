/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
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

import c8y.trackeragent.device.ManagedObjectCache;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.service.TrackerDeviceContextService;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static c8y.trackeragent.utils.SDKExceptionHandler.handleSDKException;
import static java.util.concurrent.TimeUnit.SECONDS;

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
    
    private final TrackerDeviceContextService contextService;
    private final DeviceCredentials tenantCredentials;
    private final OperationExecutor operationHelper;
    private volatile ScheduledFuture<?> future;


    public OperationDispatcher(DeviceCredentials tenantCredentials,  
    		TrackerDeviceContextService contextService, OperationExecutor operationHelper) throws SDKException {
		this.tenantCredentials = tenantCredentials;
        this.contextService = contextService;
		this.operationHelper = operationHelper;
    }
    
    public void startPolling(ScheduledExecutorService operationsExecutor) {
        future = operationsExecutor.scheduleWithFixedDelay(this, POLLING_DELAY, POLLING_INTERVAL, SECONDS);
    }

    @Override
    public void run() {
        logger.trace("Executing queued operations");
        try {
            contextService.enterContext(tenantCredentials.getTenant());
            executePendingOps();
            contextService.leaveContext();
        } catch (Exception x) {
            logger.warn("Error while executing operations", x);
        }
    }

    private void executePendingOps() throws SDKException {
        logger.debug("Querying for pending operations");
        for (OperationRepresentation operation : getOperationsByStatus(OperationStatus.PENDING)) {
        	// TODO lest enter the context here 
        	GId deviceId = operation.getDeviceId();
        	TrackerDevice device = ManagedObjectCache.instance().get(deviceId);
        	if (device == null) {
        		logger.trace("Ignore operation with ID {} -> device with id {} hasn't been identified yet", operation.getId(), deviceId);
        		continue; // Device hasn't been identified yet
        	}
        	contextService.enterContext(tenantCredentials.getTenant(), device.getImei());
        	try {
        		operationHelper.execute(operation, device);
        	} finally {
        		contextService.leaveContext();
        	}
        }
    }
    
    private Iterable<OperationRepresentation> getOperationsByStatus(OperationStatus status) throws SDKException {
        Iterable<OperationRepresentation> operationsIterable = Collections.emptyList();
        try {
            operationsIterable = operationHelper.getOperationsByStatusAndAgent(status);
//            when tenant is disabled then thrown exception is BeanInstantiationException with SDKException with 401 status as a cause
        } catch (final Exception e) {
//            404 - someone deleted device, 401 tenant is disabled
            switch (handleSDKException(e, 401, 404)) {
                case OTHER_EXCEPTION:
                    throw e;
                case STATUS_MATCHES:
                    if (future != null) {
                        future.cancel(false);
                    }
            }
        }
        return operationsIterable;
    }

}
