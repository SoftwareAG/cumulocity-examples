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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import c8y.lx.driver.Driver;

import com.tinkerforge.IPConnection;
import com.tinkerforge.IPConnection.EnumerateListener;

/**
 * Discovers all connected bricklets using the enumeration functionality of
 * TinkerForge and notifies a client when the enumeration is finished. The
 * enumeration is considered finished when a timeout after the last discovery of
 * a device is exceeded.
 */
public class Discoverer extends TimerTask implements EnumerateListener {

	public interface DiscoveryFinishedListener {
		void discoveredDevices(List<Driver> devices);
	}

	public Discoverer(BrickletFactory factory,
			DiscoveryFinishedListener finished, long timeout) {
		this.factory = factory;
		this.finished = finished;
		this.timeout = timeout;
	}

	public void enumerate(String uid, String connectedUid, char position,
			short[] hardwareVersion, short[] firmwareVersion, int deviceId,
			short enumerationType) {
		if (enumerationType == IPConnection.ENUMERATION_TYPE_CONNECTED
				|| enumerationType == IPConnection.ENUMERATION_TYPE_AVAILABLE) {
			timeoutTimer.cancel();

			Driver driver = factory.produceDevice(uid, deviceId);
			devices.add(driver);

			timeoutTimer.schedule(this, timeout);
		}

	}

	@Override
	public void run() {
		finished.discoveredDevices(devices);
	}

	private BrickletFactory factory;
	private DiscoveryFinishedListener finished;
	private long timeout;
	private Timer timeoutTimer = new Timer("DiscoveryTimeout");
	private List<Driver> devices = new ArrayList<Driver>();
}
