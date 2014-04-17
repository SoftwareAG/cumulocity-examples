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

import c8y.Barometer;
import c8y.BarometerMeasurement;

import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.Device;

public class BarometerBricklet extends BaseSensorBricklet {
	private static final double ALT_SCALE = 100.0;
	private static final double PRESS_SCALE = 1000.0;
	
	private BarometerMeasurement barometer = new BarometerMeasurement();

	public BarometerBricklet(String id, Device device) {
		super(id, device, "Barometer", new Barometer());
	}
	
    @Override
    public void initialize() throws Exception {
        // Nothing to do here.
    }

	@Override
	public void run() {
		try {
			BrickletBarometer b = (BrickletBarometer) getDevice();
			BigDecimal p = new BigDecimal((double) b.getAirPressure() / PRESS_SCALE);
			barometer.setPressure(p);
			BigDecimal a = new BigDecimal((double) b.getAltitude() / ALT_SCALE);
			barometer.setAltitude(a);
			super.sendMeasurement(barometer);
		} catch (Exception x) {
			logger.warn("Cannot read barometer bricklet", x);
		}
	}
}
