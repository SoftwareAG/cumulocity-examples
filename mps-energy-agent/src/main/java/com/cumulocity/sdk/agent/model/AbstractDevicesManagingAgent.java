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
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.agent.util.PeekBlockingQueueWrapper;
import com.cumulocity.sdk.agent.util.UniqueElementsQueueWrapper;
import com.cumulocity.sdk.agent.util.OperationRepresentationByIdComparator;

/**
 * Base implementation of an agent that manages some devices.
 * @param <D> type of devices managed by the agent.
 */
public abstract class AbstractDevicesManagingAgent<D extends Device> extends AbstractAgent 
		implements DevicesManagingAgent<D> {

	private volatile Map<GId, D> devicesMap = 
			new ConcurrentHashMap<GId, D>();
	
	private Queue<MeasurementRepresentation> measurementsQueue = 
			new ConcurrentLinkedQueue<MeasurementRepresentation>();
	
	private Queue<OperationRepresentation> operationsQueue =
			new PeekBlockingQueueWrapper<OperationRepresentation>(
			new UniqueElementsQueueWrapper<OperationRepresentation>(
					new ConcurrentLinkedQueue<OperationRepresentation>(),
					OperationRepresentationByIdComparator.getInstance()));
	
	@Override
	public Collection<D> getDevices() {
		return devicesMap.values();
	}
	
	@Override
	public D getDevice(GId gid) {
		return devicesMap.get(gid);
	}

	public void setDevices(Collection<D> devices) {
		Map<GId, D> map = new ConcurrentHashMap<GId, D>(devices.size());
		for (D device : devices) {
			map.put(device.getGlobalId(), device);
		}
		devicesMap = map;
	}
	
	@Override
	public Queue<MeasurementRepresentation> getMeasurementsQueue() {
		return measurementsQueue;
	}
	
	@Override
	public Queue<OperationRepresentation> getOperationsQueue() {
		return operationsQueue;
	}
}
