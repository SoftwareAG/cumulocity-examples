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

import java.io.IOException;
import java.net.UnknownHostException;

import c8y.tinkerforge.Discoverer.DiscoveryFinishedListener;

import c8y.tinkerforge.bricklet.BrickletFactory;
import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;
import com.tinkerforge.IPConnection.ConnectedListener;
import com.tinkerforge.NotConnectedException;

/**
 * Connects to TinkerForge and starts enumeration.
 */
public class BrickConnector implements ConnectedListener {
	public static final String DEVHOST = "localhost";
	public static final int DEVPORT = 4223;

	private IPConnection ipcon;
	private Discoverer discoverer;
	private DiscoveryFinishedListener finished;

	public BrickConnector(DiscoveryFinishedListener finished)
			throws UnknownHostException, AlreadyConnectedException, IOException {
		this.finished = finished;
		
		ipcon = new IPConnection();
		ipcon.addConnectedListener(this);
		ipcon.connect(DEVHOST, DEVPORT);
	}

	@Override
	public void connected(short connectReason) {
		try {
			resetDiscoverer();
			ipcon.enumerate();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	private void resetDiscoverer() {
		if (discoverer != null) {
			ipcon.removeEnumerateListener(discoverer);
		}

		BrickletFactory factory = new BrickletFactory(ipcon);
		discoverer = new Discoverer(factory, finished, ipcon.getTimeout());

		ipcon.addEnumerateListener(discoverer);
	}
}
