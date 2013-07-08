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

import c8y.Geofence;
import c8y.IsDevice;
import c8y.Position;
import c8y.SupportedOperations;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.event.EventApi;

public class TrackerDevice extends DeviceManagedObject {
	public static final String TYPE = "c8y_Tracker";
	public static final String XTID_TYPE = "c8y_Imei";
	public static final String EVENT_TYPE = "c8y_LocationUpdate";

	public TrackerDevice(Platform platform, GId agentGid, String imei)
			throws SDKException {
		super(platform);
		this.events = platform.getEventApi();
		this.imei = imei;
		setupMo(agentGid);
		setupEvents(agentGid);
	}

	public String getImei() {
		return imei;
	}

	public GId getGId() {
		return gid;
	}

	public void setLocation(BigDecimal latitude, BigDecimal longitude,
			BigDecimal altitude) throws SDKException {
		position.setLat(latitude);
		position.setLng(longitude);
		position.setAlt(altitude);
		getInventory().getManagedObject(gid).update(device);

		locationUpdate.setTime(new Date());
		events.create(locationUpdate);
	}

	public void geofenceAlarm(String imei2, boolean equals) {
		// TODO Auto-generated method stub

	}

	public void setGeofence(String imei2, Geofence ackedFence) {
		// TODO Auto-generated method stub

	}

	private void setupEvents(GId agentGid) throws SDKException {
		this.locationUpdate.setType(EVENT_TYPE);
		this.locationUpdate.setText("Location updated");
		this.locationUpdate.set(position);

		ManagedObjectRepresentation source = new ManagedObjectRepresentation();
		source.setId(gid);
		source.setSelf(device.getSelf());
		locationUpdate.setSource(source);
	}

	private void setupMo(GId agentGid) throws SDKException {
		SupportedOperations ops = ConnectionRegistry.instance().get(imei)
				.getSupportedOperations();
		this.device.set(ops);
		this.device.set(new IsDevice());
		this.device.set(position);

		ID extId = new ID(imei);
		extId.setType(XTID_TYPE);

		device.setType(TYPE);
		device.setName("Tracker " + imei);

		createOrUpdate(device, extId, agentGid);
		gid = device.getId();
		device.setId(null); // Ugly ugly
	}

	private EventApi events;
	private String imei;
	private GId gid;
	private ManagedObjectRepresentation device = new ManagedObjectRepresentation();
	private Position position = new Position();
	private EventRepresentation locationUpdate = new EventRepresentation();
}
