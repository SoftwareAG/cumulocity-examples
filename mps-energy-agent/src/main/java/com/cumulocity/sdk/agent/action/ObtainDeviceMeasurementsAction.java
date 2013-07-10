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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.agent.driver.DeviceDriver;
import com.cumulocity.sdk.agent.driver.DeviceException;
import com.cumulocity.sdk.agent.model.Device;
import com.cumulocity.sdk.agent.model.DevicesManagingAgent;

/**
 * Base implementation of action retrieving device measurements.
 * @param <D> type of devices managed by the agent.
 */
public class ObtainDeviceMeasurementsAction<D extends Device> implements AgentAction {

    private static final Logger LOG = LoggerFactory.getLogger(ObtainDeviceMeasurementsAction.class);
    
	private DevicesManagingAgent<D> agent;
    
    private DeviceDriver<D> deviceDriver;
	
    /**
     * @param agent the agent controlling some devices to get measurements from.
     */
    @Autowired
	public ObtainDeviceMeasurementsAction(DevicesManagingAgent<D> agent) {
		this.agent = agent;
	}
    
    public void setDeviceDriver(DeviceDriver<D> deviceDriver) {
		this.deviceDriver = deviceDriver;
	}

	@Override
	public void run() {
		Collection<D> devices = new ArrayList<D>(agent.getDevices());
		
		for (D device : devices) {
		    try {
    			List<MeasurementRepresentation> measurements = deviceDriver.loadMeasuremntsFromDevice(device);
    			agent.getMeasurementsQueue().addAll(measurements);
		    } catch (DeviceException e) {
		        LOG.error("Device error when obtaining measurements!", e);
		    }
		}
	}
	
}
