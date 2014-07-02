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

import com.tinkerforge.BrickletPTC;
import com.tinkerforge.Device;

public class PTCBricklet extends BaseSensorBricklet {

	private BrickletPTC ptc;
	private TemperatureMeasurement temperature;
	
	public PTCBricklet(String id, Device device) {
		super(id, device, "Temperature", new TemperatureSensor());
	}

	@Override
	public void initialize() throws Exception {
		ptc=(BrickletPTC)getDevice();
		// detect wire mode, default wire mode is 2
		if(!ptc.isSensorConnected()) {
			ptc.setWireMode(BrickletPTC.WIRE_MODE_3);
			if(!ptc.isSensorConnected()) {
				ptc.setWireMode(BrickletPTC.WIRE_MODE_4);
				if(!ptc.isSensorConnected()) {
					logger.error("No Pt100/Pt1000 sensor connected, the sensor is connected incorrectly, or the sensor itself is faulty.");
					throw new Exception("Error initializing PTC Bricklet");
				} else { logger.info("Pt100/Pt1000 sensor connected in 4-wire mode."); }
			} else { logger.info("Pt100/Pt1000 sensor connected in 3-wire mode."); }
		} else { logger.info("Pt100/Pt1000 sensor connected in 2-wire mode."); };
	}

	@Override
	public void run() {
		try {
			temperature.setTemperature(new BigDecimal((double)ptc.getTemperature()/100));
			super.sendMeasurement(temperature);
		} catch(Exception e) {
			logger.warn("Cannot read temperature from PTC bricklet", e);
		}
	}

}
