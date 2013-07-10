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

package com.cumulocity.sdk.agent.model;

import java.util.Collection;
import java.util.Queue;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;

/**
 * Defines an agent managing some devices.
 * @param <D> type of devices managed by the agent.
 */
public interface DevicesManagingAgent<D extends Device> extends Agent {

	/**
	 * Gets all devices managed by the agent. 
	 * @return a collection of devices.
	 */
	Collection<D> getDevices();
	
	/**
	 * Gets a device of given ID.
	 * @param deviceId the device ID.
	 * @return the device of given ID.
	 */
	D getDevice(GId deviceId);
	
	/**
	 * Holds the queue of measurements retrieved from devices managed by the agent 
	 * to be uploaded to the <tt>Cumulocity</tt> platform.
	 * @return the queue of measurements.
	 */
	Queue<MeasurementRepresentation> getMeasurementsQueue();
	
	/**
	 * Holds the queue of operations retrieved from the <tt>Cumulocity</tt> platform
	 * to be executed on devices managed by the agent.
	 * @return the queue od commands.
	 */
	Queue<OperationRepresentation> getOperationsQueue();
}
