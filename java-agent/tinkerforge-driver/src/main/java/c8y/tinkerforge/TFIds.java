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

import java.util.Arrays;

import c8y.Hardware;

import com.cumulocity.model.ID;
import com.tinkerforge.Device;
import com.tinkerforge.Device.Identity;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

/**
 * Utility class for generating various kinds of names and IDs for TinkerForge devices.
 */
public class TFIds {

	public static final String XTIDTYPE = "c8y_Serial";

	public static String getType(String type) {
		return "c8y_TinkerForge_" + type;
	}

	public static String getDefaultName(String parentName, String type, String id) {
		return parentName + " " + type + " " + id;
	}

	public static ID getXtId(String brickletId, String parentId) {
        ID extId = new ID("tinkerforge-"+ parentId + "-" + brickletId);
        extId.setType(XTIDTYPE);
        return extId;
	}

	public static String getMeasurementType(String type) {
		return "c8y_" + type + "Measurement";
	}

	public static String getPropertyName(String type) {
		return "c8y." + type.toLowerCase();
	}
	
	public static Hardware getHardware(Device device, String type) throws TimeoutException, NotConnectedException {
        String model = "TF" + type + "Bricklet";
        Identity identity = device.getIdentity();
        String revision = Arrays.toString(identity.hardwareVersion);
		return new Hardware(model, identity.uid, revision);
	}
}
