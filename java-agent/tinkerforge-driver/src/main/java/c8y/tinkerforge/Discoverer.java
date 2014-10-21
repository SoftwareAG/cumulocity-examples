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

package c8y.tinkerforge;

import static com.tinkerforge.IPConnection.ENUMERATION_TYPE_AVAILABLE;
import static com.tinkerforge.IPConnection.ENUMERATION_TYPE_CONNECTED;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.lx.driver.Driver;
import c8y.lx.driver.RestartableTimerTask;
import c8y.tinkerforge.bricklet.BrickletFactory;

import com.tinkerforge.IPConnection.EnumerateListener;

/**
 * Discovers all connected bricklets using the enumeration functionality of
 * TinkerForge and notifies a client when the enumeration is finished. The
 * enumeration is considered finished when a timeout after the last discovery of
 * a device is exceeded.
 */
public class Discoverer implements EnumerateListener, Runnable {

    public static interface DiscoveryFinishedListener {

        void discoveredDevices(Iterable<Driver> devices);

    }

    private static Logger logger = LoggerFactory.getLogger(Discoverer.class);
    
    private final BrickletFactory factory;

    private final DiscoveryFinishedListener finished;

    private final long timeout;

    private final List<Driver> devices = new LinkedList<>();

    private Timer timeoutTimer;

	public Discoverer(BrickletFactory factory, DiscoveryFinishedListener finished, long timeout) {
		this.factory = factory;
		this.finished = finished;
		this.timeout = timeout;
		startTimer();
	}

	public void enumerate(String uid, String connectedUid, char position, short[] hardwareVersion,
            short[] firmwareVersion, int deviceId, short enumerationType) {

		if (ENUMERATION_TYPE_CONNECTED == enumerationType ||
                ENUMERATION_TYPE_AVAILABLE == enumerationType) {
			timeoutTimer.cancel();

			try {
				Driver driver = factory.produceDevice(uid, deviceId);
				if (driver != null) {
					devices.add(driver);
				}
			} catch (IllegalArgumentException e) {
				logger.warn("Unsuported device identifier: "+ deviceId);
			}
			startTimer();
		}

	}
	
	private void startTimer() {
		timeoutTimer = new Timer("BrickDiscoveryTimeout");
		timeoutTimer.schedule(new RestartableTimerTask(this), timeout);
	}

	@Override
	public void run() {
		finished.discoveredDevices(devices);
	}
}
