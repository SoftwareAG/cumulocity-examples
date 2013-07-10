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

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.agent.model.DevicesManagingAgent;
import com.cumulocity.sdk.client.Platform;

/**
 * Uploads the device measurements to the platform.
 */
public class UploadDeviceMeasurementsAction implements AgentAction {

    private Platform platform;

    private DevicesManagingAgent<?> agent;

    @Autowired
    public UploadDeviceMeasurementsAction(Platform platform, DevicesManagingAgent<?> agent) {
        this.platform = platform;
        this.agent = agent;
    }

    @Override
    public void run() {
        MeasurementRepresentation measurement = agent.getMeasurementsQueue().poll();
        while (measurement != null) {
            uploadMeasurmentToPlatform(measurement);
            measurement = agent.getMeasurementsQueue().poll();
        }
    }

    protected void uploadMeasurmentToPlatform(MeasurementRepresentation measurement) {
        try {
            platform.getMeasurementApi().create(measurement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
