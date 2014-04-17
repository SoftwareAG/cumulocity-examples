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

package c8y.tinkerforge.bricklet;

import java.math.BigDecimal;

import c8y.TemperatureMeasurement;
import c8y.TemperatureSensor;

import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.Device;

public class TemperatureBricklet extends BaseSensorBricklet {

	private TemperatureMeasurement temperature = new TemperatureMeasurement();

	public TemperatureBricklet(String id, Device device) {
		super(id, device, "Temperature", new TemperatureSensor());
	}
	
    @Override
    public void initialize() throws Exception {
        // Nothing to do here.
    }

	@Override
	public void run() {
		try {
			BrickletTemperature tb = (BrickletTemperature) getDevice();
			BigDecimal t = new BigDecimal((double) tb.getTemperature() / 100.0);
			temperature.setTemperature(t);
			super.sendMeasurement(temperature);
		} catch (Exception x) {
			logger.warn("Cannot read temperature from bricklet", x);
		}
	}
}
