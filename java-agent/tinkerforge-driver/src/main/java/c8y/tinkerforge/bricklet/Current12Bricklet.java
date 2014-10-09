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

import c8y.CurrentMeasurement;
import c8y.CurrentSensor;

import com.tinkerforge.BrickletCurrent12;
import com.tinkerforge.Device;

public class Current12Bricklet extends BaseSensorBricklet {
	
	private CurrentMeasurement current = new CurrentMeasurement();

	public Current12Bricklet(String id, Device device) {
		super(id, device, "Current", new CurrentSensor());
	}

	@Override
	public void initialize() throws Exception {
		// Nothing to be done.
	}

	@Override
	public void run() {
		try {
			BrickletCurrent12 currentBricklet = (BrickletCurrent12)getDevice();
			BigDecimal currentValue = new BigDecimal( 
					(double)currentBricklet.getCurrent() / 1000 );
			current.setCurrentValue(currentValue);
			super.sendMeasurement(current);
		} catch (Exception x) {
			logger.warn("Cannot read current from bricklet", x);
		}
	}

}
