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
import java.util.Date;

import c8y.Position;

import com.cumulocity.model.ID;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.event.EventApi;

public class TrackerDevice extends DeviceManagedObject {
	public static final String TYPE = "c8y_Tracker";
	public static final String XTID_TYPE = "c8y_Imei";
	public static final String EVENT_TYPE = "c8y_LocationUpdate";
	
	public TrackerDevice(Platform platform, String imei) {
		super(platform);
		this.events = platform.getEventApi();

		this.imei = imei;

		this.device.set(position);
		
		this.locationUpdate.setType(EVENT_TYPE);
		this.locationUpdate.setText("Location updated");
		this.locationUpdate.set(position);
	}

	public String getImei() {
		return imei;
	}

	public void setLocation(BigDecimal latitude, BigDecimal longitude,
			BigDecimal altitude) throws SDKException {
		position.setLatitude(latitude);
		position.setLongitude(longitude);
		position.setAltitude(altitude);
		
		updateInventory();
		
		locationUpdate.setTime(new Date());
		events.create(locationUpdate);
	}

	private void updateInventory() throws SDKException {
		if (device.getId() == null) {
			ID extId = new ID(imei);
			extId.setType(XTID_TYPE);
			
			device.setType(TYPE);
			device.setName("Tracker " + imei);

			createOrUpdate(device, extId, null);
			
			ManagedObjectRepresentation source = new ManagedObjectRepresentation();
			source.setId(device.getId());
			source.setSelf(device.getSelf());
			locationUpdate.setSource(source);
		} else {
			getInventory().getManagedObject(device.getId()).update(device);
		}
	}

	private EventApi events;
	private String imei;
	private ManagedObjectRepresentation device = new ManagedObjectRepresentation();
	private Position position = new Position();
	private EventRepresentation locationUpdate = new EventRepresentation();
}
