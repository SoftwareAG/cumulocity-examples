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

import c8y.HumidityMeasurement;
import c8y.HumiditySensor;
import c8y.TemperatureMeasurement;
import com.tinkerforge.BrickletHumidityV2;
import com.tinkerforge.Device;

import java.math.BigDecimal;

public class HumidityBrickletV2 extends BaseSensorBricklet {

	private HumidityMeasurement humidity = new HumidityMeasurement();
	private TemperatureMeasurement temperature = new TemperatureMeasurement();

	public HumidityBrickletV2(String id, Device device) {
		super(id, device, "Humidity", new HumiditySensor());
	}
	
    @Override
    public void initialize() throws Exception {
        // Nothing to do here.
    }

	@Override
	public void run() {
		try {
			BrickletHumidityV2 hb = (BrickletHumidityV2) getDevice();
			BigDecimal h = new BigDecimal((double) hb.getHumidity() / 100.0);
			humidity.setHumidity(h);
			super.sendMeasurement(humidity);
			BigDecimal t = new BigDecimal((double) hb.getTemperature() / 100.0);
			temperature.setTemperature(t);
			super.sendMeasurement(temperature);
		} catch (Exception x) {
			logger.warn("Cannot read humidity or temperature from bricklet", x);
		}
	}
}
