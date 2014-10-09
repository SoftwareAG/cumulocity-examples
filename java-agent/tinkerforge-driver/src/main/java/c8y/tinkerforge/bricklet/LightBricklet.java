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

import c8y.LightMeasurement;
import c8y.LightSensor;

import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.Device;

public class LightBricklet extends BaseSensorBricklet {

	private LightMeasurement light = new LightMeasurement();

	public LightBricklet(String id, Device device) {
		super(id, device, "Light", new LightSensor());
	}
	
    @Override
    public void initialize() throws Exception {
        // Nothing to do here.
    }

	@Override
	public void run() {
		try {
			BrickletAmbientLight lb = (BrickletAmbientLight) getDevice();
			BigDecimal l = new BigDecimal((double) lb.getIlluminance() / 10.0);
			light.setIlluminance(l);
			super.sendMeasurement(light);
		} catch (Exception x) {
			logger.warn("Cannot read illuminance from bricklet", x);
		}
	}
}
