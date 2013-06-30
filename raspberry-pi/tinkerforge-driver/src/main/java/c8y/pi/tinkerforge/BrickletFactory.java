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


package c8y.pi.tinkerforge;

import c8y.pi.driver.Driver;

import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.Device;
import com.tinkerforge.IPConnection;

/**
 * Instantiates the correct driver for a discovered bricklet. 
 */
public class BrickletFactory {
	public BrickletFactory(IPConnection ipcon) {
		this.ipcon = ipcon;
	}

	public Driver produceDevice(String uid, int deviceIdentifier) {
		Device d = null;
		switch (deviceIdentifier) {
/*		case BrickDC.DEVICE_IDENTIFIER:
			return new BrickDC(uid, ipcon);
		case BrickIMU.DEVICE_IDENTIFIER:
			return new BrickIMU(uid, ipcon);
		case BrickletAnalogIn.DEVICE_IDENTIFIER:
			return new BrickletAnalogIn(uid, ipcon);
		case BrickletAnalogOut.DEVICE_IDENTIFIER:
			return new BrickletAnalogOut(uid, ipcon);
		case BrickletCurrent12.DEVICE_IDENTIFIER:
			return new BrickletCurrent12(uid, ipcon);
		case BrickletCurrent25.DEVICE_IDENTIFIER:
			return new BrickletCurrent25(uid, ipcon);
		case BrickletDistanceIR.DEVICE_IDENTIFIER:
			return new BrickletDistanceIR(uid, ipcon);
		case BrickletDualRelay.DEVICE_IDENTIFIER:
			return new BrickletDualRelay(uid, ipcon);
		case BrickletGPS.DEVICE_IDENTIFIER:
			return new BrickletGPS(uid, ipcon);
		case BrickletHumidity.DEVICE_IDENTIFIER:
			return new BrickletHumidity(uid, ipcon);
		case BrickletIndustrialDigitalIn4.DEVICE_IDENTIFIER:
			return new BrickletIndustrialDigitalIn4(uid, ipcon);
		case BrickletIndustrialDigitalOut4.DEVICE_IDENTIFIER:
			return new BrickletIndustrialDigitalOut4(uid, ipcon);
		case BrickletIndustrialQuadRelay.DEVICE_IDENTIFIER:
			return new BrickletIndustrialQuadRelay(uid, ipcon);
		case BrickletIO16.DEVICE_IDENTIFIER:
			return new BrickletIO16(uid, ipcon);
		case BrickletIO4.DEVICE_IDENTIFIER:
			return new BrickletIO4(uid, ipcon);
		case BrickletJoystick.DEVICE_IDENTIFIER:
			return new BrickletJoystick(uid, ipcon);
		case BrickletLCD16x2.DEVICE_IDENTIFIER:
			return new BrickletLCD16x2(uid, ipcon);
		case BrickletLinearPoti.DEVICE_IDENTIFIER:
			return new BrickletLinearPoti(uid, ipcon);
		case BrickletPiezoBuzzer.DEVICE_IDENTIFIER:
			return new BrickletPiezoBuzzer(uid, ipcon);
		case BrickletPTC.DEVICE_IDENTIFIER:
			return new BrickletPTC(uid, ipcon);
		case BrickletRotaryPoti.DEVICE_IDENTIFIER:
			return new BrickletRotaryPoti(uid, ipcon);
		case BrickletTemperatureIR.DEVICE_IDENTIFIER:
			return new BrickletTemperatureIR(uid, ipcon);
		case BrickletVoltage.DEVICE_IDENTIFIER:
			return new BrickletVoltage(uid, ipcon);
		case BrickletVoltageCurrent.DEVICE_IDENTIFIER:
			return new BrickletVoltageCurrent(uid, ipcon);
		case BrickServo.DEVICE_IDENTIFIER:
			return new BrickServo(uid, ipcon);
		case BrickStepper.DEVICE_IDENTIFIER:
			return new BrickStepper(uid, ipcon);
		case BrickletAmbientLight.DEVICE_IDENTIFIER:
			d = new BrickletAmbientLight(uid, ipcon);
			return new LightMapper(d, uid, uid);
		case BrickletHumidity.DEVICE_IDENTIFIER:
			d = new BrickletHumidity(uid, ipcon);
			return new HumidityMapper(d, uid, uid);
		case BrickMaster.DEVICE_IDENTIFIER:
			d = new BrickMaster(uid, ipcon);
			return new MasterMapper(d, uid, uid);*/
		case BrickletBarometer.DEVICE_IDENTIFIER:
			return new BarometerBricklet(uid, new BrickletBarometer(uid, ipcon));
		case BrickletHumidity.DEVICE_IDENTIFIER:
			return new HumidityBricklet(uid, new BrickletHumidity(uid, ipcon));
		case BrickletLCD20x4.DEVICE_IDENTIFIER:
			return new DisplayBricklet(uid, new BrickletLCD20x4(uid, ipcon));
		case BrickletTemperature.DEVICE_IDENTIFIER:
			return new TemperatureBricklet(uid, new BrickletTemperature(uid, ipcon));
		default:
			throw new IllegalArgumentException("Unknown device identifier: "
					+ deviceIdentifier);
		}
	}

	private IPConnection ipcon;
}
