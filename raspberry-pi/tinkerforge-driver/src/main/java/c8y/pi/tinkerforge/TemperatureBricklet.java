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

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.pi.driver.DeviceManagedObject;
import c8y.pi.driver.PollingDriver;

import com.cumulocity.model.ID;
import com.cumulocity.model.environmental.sensor.TemperatureSensor;
import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.Device;

public class TemperatureBricklet extends PollingDriver {
	public static final String TYPE = "c8y_TinkerForgeTemperature";
	public static final String XTIDTYPE = "c8y_Serial";
	public static final long POLLING_INTERVAL = 5000;

	public TemperatureBricklet(String id, Device device) {
		super("c8y_TemperatureMeasurement", "c8y.temperaturePolling", POLLING_INTERVAL);
		this.id = id;
		this.device = device;
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation parent) {
		mo.set(new TemperatureSensor());

		ID extId = new ID("tinkerforge-" + id);
		extId.setType(XTIDTYPE);
		String defaultName = parent.getName() + " Temperature " + id;
		DeviceManagedObject dmo = new DeviceManagedObject(platform, extId);
		try {
			boolean created = dmo.createOrUpdate(mo, defaultName, TYPE);
			if (created) {
				ManagedObjectReferenceRepresentation moRef = new ManagedObjectReferenceRepresentation();
				moRef.setManagedObject(mo);
				platform.getInventoryApi().getManagedObject(parent.getId())
						.addChildDevice(moRef);
			}
		} catch (SDKException e) {
			logger.warn("Cannot create managed object", e);
		}
		
		super.setSource(mo);
	}
	
	@Override
	protected void createMeasurementTemplate(Map<String,MeasurementValue> measurement) {
		temperature.setUnit("C");
		measurement.put("T", temperature);
	}
	
	@Override
	public void run() {
		try {
			BrickletTemperature tb = (BrickletTemperature) device;
			BigDecimal t = new BigDecimal((double) tb.getTemperature() / 100.0);
			temperature.setValue(t);
			super.sendMeasurement();
		} catch (Exception x) {
			logger.warn("Cannot read temperature from bricklet", x);
		}
	}

	private static Logger logger = LoggerFactory
			.getLogger(TemperatureBricklet.class);

	private String id;
	private Device device;
	private Platform platform;
	private ManagedObjectRepresentation mo = new ManagedObjectRepresentation();
	private MeasurementValue temperature = new MeasurementValue();
}
