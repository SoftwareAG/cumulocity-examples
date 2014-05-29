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
package c8y.lx.driver;

import java.util.Date;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.measurement.MeasurementApi;

public abstract class MeasurementPollingDriver extends PollingDriver {

    private MeasurementApi measurements;
    private MeasurementRepresentation measurementRep = new MeasurementRepresentation();

    public MeasurementPollingDriver(String measurementType, String pollingProp,
                                    long defaultPollingInterval) {
        super(measurementType, pollingProp, defaultPollingInterval);
        measurementRep.setType(measurementType);
    }

    @Override
    public void initialize(Platform platform) throws Exception {
        super.initialize(platform);
        this.measurements = platform.getMeasurementApi();
    }

    public void setSource(ManagedObjectRepresentation mo) {
        measurementRep.setSource(mo);
    }

    protected void sendMeasurement(Object measurement) {
        try {
            measurementRep.set(measurement);
            measurementRep.setTime(new Date());
            measurements.create(measurementRep);
        } catch (SDKException e) {
            logger.warn("Cannot send measurement", e);
        }
    }
}
