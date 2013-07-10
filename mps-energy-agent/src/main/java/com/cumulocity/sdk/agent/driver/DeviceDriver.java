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

package com.cumulocity.sdk.agent.driver;

import java.util.List;

import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.agent.model.Device;

/**
 * A {@link Device} driver that obtains measurements from device and executes an action on a device.
 *
 * @param <D> type of devices managed by the agent.
 */
public interface DeviceDriver<D extends Device> {

    /**
     * Loads the measurements from the device.
     *
     * @param device the MPS device.
     * @return list of retrieved operations.
     * @throws DeviceException in case of device error.
     */
    List<MeasurementRepresentation> loadMeasuremntsFromDevice(D device) throws DeviceException;

    /**
     * Checks if operationRep is supported by the devices controlled by the agent.
     *
     * @param operation the operationRep to check.
     * @return <code>true</code> if operationRep is supported.
     */
    boolean isOperationSupported(OperationRepresentation operation);

    /**
     * Processes the valid operationRep execution.
     *
     * @param operation the operationRep to execute.
     * @throws DeviceException in case of failed operationRep execution.
     */
    void handleSupportedOperation(OperationRepresentation operation) throws DeviceException;

}