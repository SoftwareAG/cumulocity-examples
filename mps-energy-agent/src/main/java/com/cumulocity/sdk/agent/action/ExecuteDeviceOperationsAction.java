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

package com.cumulocity.sdk.agent.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.agent.driver.DeviceDriver;
import com.cumulocity.sdk.agent.driver.DeviceException;
import com.cumulocity.sdk.agent.model.Device;
import com.cumulocity.sdk.agent.model.DevicesManagingAgent;
import com.cumulocity.sdk.client.Platform;

/**
 * Base implementation of an action executing commands on a device.
 * @param <D> type of devices managed by the agent.
 */
public class ExecuteDeviceOperationsAction<D extends Device> implements AgentAction {

    private static final Logger LOG = LoggerFactory.getLogger(ExecuteDeviceOperationsAction.class);

    private Platform platform;

    private DevicesManagingAgent<D> agent;

    private DeviceDriver<D> deviceDriver;

    /**
     * @param platform the <tt>Cumulocity</tt> platform.
     * @param agent the agent controlling some devices to run actions on.
     */
    @Autowired
    public ExecuteDeviceOperationsAction(Platform platform, DevicesManagingAgent<D> agent) {
        this.platform = platform;
        this.agent = agent;
    }

    public void setDeviceDriver(DeviceDriver<D> deviceDriver) {
        this.deviceDriver = deviceDriver;
    }

    @Override
    public void run() {
        OperationRepresentation operation = null;
        // this implementation assumes that peek() is blocking the queue until poll() is called
        // @see PeekBlockingQueueWrapper
        while ((operation = agent.getOperationsQueue().peek()) != null) {
            LOG.info(String.format("Running operationRep %s...", operation.getId().toJSON()));
            try {
                setOperationStatus(operation, OperationStatus.EXECUTING);

                handleOperation(operation);

                handleOperationSuccess(operation);

            } catch (Exception e) {
                handleOperationFailure(operation, e);
            } finally {
                // remove the operationRep from queue after it was processed
                // to avoid adding it again by another thread in meantime
                agent.getOperationsQueue().poll();
            }
        }
    }

    /**
     * Processes the operationRep execution. Checks if operationRep is supported and if so 
     * than passes to {@link #handleSupportedOperation(OperationRepresentation)}.
     * @param operationRep the operationRep to execute.
     * @return <code>true</code> if the operationRep was supported and executed successfully.
     * @throws DeviceException in case of failed operationRep execution.
     */
    protected void handleOperation(OperationRepresentation operation) throws DeviceException {
        if (!deviceDriver.isOperationSupported(operation)) {
            throw new DeviceException("Operation is unsupported!");
        }
        deviceDriver.handleSupportedOperation(operation);
    }

    /**
     * Processes the operationRep success.
     * @param operationRep the operationRep that succeeded.
     * @throws Exception in case of success handling error.
     */
    protected void handleOperationSuccess(OperationRepresentation operation) throws Exception {
        setOperationStatus(operation, OperationStatus.SUCCESSFUL);
    }

    /**
     * Processes the operationRep failure.
     * @param operationRep the operationRep that failed.
     * @param failureException the cause of failure.
     */
    protected void handleOperationFailure(OperationRepresentation operation, Exception failureException) {
        String failureReason = (failureException == null ? "Unknown failure reason!" : failureException.getMessage());
        LOG.error(failureReason, failureException);
        try {
            setOperationFailedStatus(operation, failureReason);
        } catch (Exception ex) {
            LOG.error("Unable to set operationRep 'FAILED' status!", ex);
        }
    }

    protected void setOperationStatus(OperationRepresentation operation, OperationStatus status) throws Exception {
        operation.setStatus(status.toString());
        platform.getDeviceControlApi().update(operation);
    }

    /**
     * Sets the 'FAILED' operationRep status.
     * @param operationRep the operationRep to change status for.
     * @param failureReason if operationRep is failed
     */
    protected void setOperationFailedStatus(OperationRepresentation operation, String failureReason) throws Exception {
        operation.setFailureReason(failureReason);
        setOperationStatus(operation, OperationStatus.FAILED);
    }
}
