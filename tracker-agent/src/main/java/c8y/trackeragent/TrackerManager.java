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

package c8y.trackeragent;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;

public class TrackerManager {
	public TrackerManager(Platform platform) {
		this.platform = platform;
	}

	public void locationUpdate(String imei, BigDecimal latitude,
			BigDecimal longitude, BigDecimal altitude) throws SDKException {
		getOrCreate(imei).setLocation(latitude, longitude, altitude);
	}

	public TrackerDevice getOrCreate(String imei) {
		TrackerDevice device = devices.get(imei);

		if (device == null) {
			device = new TrackerDevice(platform, imei);
			devices.put(imei, device);
		}

		return device;
	}

	private Platform platform;
	private Map<String, TrackerDevice> devices = new ConcurrentHashMap<String, TrackerDevice>();
}
