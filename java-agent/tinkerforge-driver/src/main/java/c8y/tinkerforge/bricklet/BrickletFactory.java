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

import c8y.lx.driver.Driver;
import c8y.tinkerforge.bricklet.BarometerBricklet;
import c8y.tinkerforge.bricklet.DisplayBricklet;
import c8y.tinkerforge.bricklet.DistanceIRBricklet;
import c8y.tinkerforge.bricklet.DualRelayBricklet;
import c8y.tinkerforge.bricklet.GpsBricklet;
import c8y.tinkerforge.bricklet.HumidityBricklet;
import c8y.tinkerforge.bricklet.LightBricklet;
import c8y.tinkerforge.bricklet.TemperatureBricklet;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.BrickletCurrent12;
import com.tinkerforge.BrickletCurrent25;
import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletDistanceUS;
import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.BrickletGPS;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletIO16;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletMoisture;
import com.tinkerforge.BrickletMotionDetector;
import com.tinkerforge.BrickletPTC;
import com.tinkerforge.BrickletRemoteSwitch;
import com.tinkerforge.BrickletRotaryPoti;
import com.tinkerforge.BrickletSegmentDisplay4x7;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.BrickletVoltageCurrent;
import com.tinkerforge.IPConnection;

/**
 * Instantiates the correct driver for a discovered bricklet. 
 */
public class BrickletFactory {

	private IPConnection ipcon;

	public BrickletFactory(IPConnection ipcon) {
		this.ipcon = ipcon;
	}

	public Driver produceDevice(String uid, int deviceIdentifier) {
		switch (deviceIdentifier) {
/*		case BrickDC.DEVICE_IDENTIFIER:
			return new BrickDC(uid, ipcon);
		case BrickIMU.DEVICE_IDENTIFIER:
			return new BrickIMU(uid, ipcon);
		case BrickletAnalogIn.DEVICE_IDENTIFIER:
			return new BrickletAnalogIn(uid, ipcon);
		case BrickletAnalogOut.DEVICE_IDENTIFIER:
			return new BrickletAnalogOut(uid, ipcon);
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
		case BrickMaster.DEVICE_IDENTIFIER:
			d = new BrickMaster(uid, ipcon);
			return new MasterMapper(d, uid, uid);*/
		case BrickMaster.DEVICE_IDENTIFIER:
			return null;
		case BrickletAmbientLight.DEVICE_IDENTIFIER:
			return new LightBricklet(uid, new BrickletAmbientLight(uid, ipcon));
		case BrickletBarometer.DEVICE_IDENTIFIER:
			return new BarometerBricklet(uid, new BrickletBarometer(uid, ipcon));
		case BrickletDistanceIR.DEVICE_IDENTIFIER:
			return new DistanceIRBricklet(uid, new BrickletDistanceIR(uid, ipcon));
		case BrickletDualRelay.DEVICE_IDENTIFIER:
			return new DualRelayBricklet(uid, new BrickletDualRelay(uid, ipcon));
		case BrickletGPS.DEVICE_IDENTIFIER:
			return new GpsBricklet(uid, new BrickletGPS(uid, ipcon));
		case BrickletHumidity.DEVICE_IDENTIFIER:
			return new HumidityBricklet(uid, new BrickletHumidity(uid, ipcon));
		case BrickletLCD20x4.DEVICE_IDENTIFIER:
			return new DisplayBricklet(uid, new BrickletLCD20x4(uid, ipcon));
		case BrickletTemperature.DEVICE_IDENTIFIER:
			return new TemperatureBricklet(uid, new BrickletTemperature(uid, ipcon));
		case BrickletMotionDetector.DEVICE_IDENTIFIER:
			return new MotionDetectorBricklet(uid, new BrickletMotionDetector(uid, ipcon));
		case BrickletRemoteSwitch.DEVICE_IDENTIFIER:
			return new RemoteSwitchBricklet(uid, new BrickletRemoteSwitch(uid, ipcon));
		case BrickletPTC.DEVICE_IDENTIFIER:
			return new PTCBricklet(uid, new BrickletPTC(uid, ipcon));
		case BrickletSegmentDisplay4x7.DEVICE_IDENTIFIER:
			return new SegmentDisplay4x7(uid, new BrickletSegmentDisplay4x7(uid, ipcon));
		case BrickletMoisture.DEVICE_IDENTIFIER:
			return new MoistureBricklet(uid, new BrickletMoisture(uid, ipcon));
		case BrickletCurrent12.DEVICE_IDENTIFIER:
			return new Current12Bricklet(uid, new BrickletCurrent12(uid, ipcon));
		case BrickletCurrent25.DEVICE_IDENTIFIER:
			return new Current25Bricklet(uid, new BrickletCurrent25(uid, ipcon));
		case BrickletDistanceUS.DEVICE_IDENTIFIER:
			return new DistanceUSBricklet(uid, new BrickletDistanceUS(uid, ipcon));
		case BrickletIO16.DEVICE_IDENTIFIER:
			return new IO16Bricklet(uid, new BrickletIO16(uid, ipcon));
		case BrickletVoltageCurrent.DEVICE_IDENTIFIER:
			return new VoltageCurrentBricklet(uid, new BrickletVoltageCurrent(uid, ipcon));
		case BrickletRotaryPoti.DEVICE_IDENTIFIER:
			return new RotaryPotiBricklet(uid, new BrickletRotaryPoti(uid, ipcon));
			
		default:
			throw new IllegalArgumentException("Unknown device identifier: "
					+ deviceIdentifier);
		}
	}
}
