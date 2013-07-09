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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Geofence;
import c8y.IsDevice;
import c8y.Position;
import c8y.SupportedOperations;

import com.cumulocity.model.ID;
import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.model.event.CumulocitySeverities;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmCollectionRepresentation;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.PagedCollectionResource;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.alarm.AlarmFilter;
import com.cumulocity.sdk.client.event.EventApi;

public class TrackerDevice extends DeviceManagedObject {
	public static final String TYPE = "c8y_Tracker";
	public static final String XTID_TYPE = "c8y_Imei";
	public static final String EVENT_TYPE = "c8y_LocationUpdate";
	public static final String ALARM_TYPE = "c8y_GeofenceAlarm";

	public TrackerDevice(Platform platform, GId agentGid, String imei)
			throws SDKException {
		super(platform);
		this.events = platform.getEventApi();
		this.alarms = platform.getAlarmApi();

		this.imei = imei;
		createMo(agentGid);
		setupTemplates(gid);
	}

	public String getImei() {
		return imei;
	}

	public GId getGId() {
		return gid;
	}

	public void setLocation(BigDecimal latitude, BigDecimal longitude,
			BigDecimal altitude) throws SDKException {
		logger.debug("Updating location of {} to lat {}, lng {}, alt {}", imei,
				latitude, longitude, altitude);

		Position position = new Position();
		position.setLat(latitude);
		position.setLng(longitude);
		position.setAlt(altitude);

		ManagedObjectRepresentation device = new ManagedObjectRepresentation();
		device.set(position);

		getInventory().getManagedObject(gid).update(device);

		locationUpdate.setTime(new Date());
		locationUpdate.set(position);
		events.create(locationUpdate);
	}

	public void geofenceAlarm(boolean raise) throws SDKException {
		logger.debug("Geofence alarm of {} is {}", imei, raise ? "raised"
				: "cleared");

		String newStatus = raise ? CumulocityAlarmStatuses.ACTIVE.toString()
				: CumulocityAlarmStatuses.CLEARED.toString();

		AlarmRepresentation activeAlarm = findActiveAlarm();

		if (activeAlarm != null) {
			activeAlarm.setTime(new Date());
			activeAlarm.setStatus(newStatus);
			alarms.updateAlarm(activeAlarm);
		} else {
			fenceAlarm.setTime(new Date());
			fenceAlarm.setStatus(newStatus);
			alarms.create(fenceAlarm);
		}
	}
	
	public void powerOnAlarm(String imei, String name, String time) {
	    
	}

	public void powerOffAlarm(String imei, String name, String time){
	    
	}
	
	private AlarmRepresentation findActiveAlarm() throws SDKException {
		PagedCollectionResource<AlarmCollectionRepresentation> alarmQuery = alarms
				.getAlarmsByFilter(fenceAlarmFilter);
		AlarmCollectionRepresentation acr = alarmQuery.get(1000);
		List<AlarmRepresentation> alarms = acr.getAlarms();
		if (alarms.size() > 0) {
			return alarms.get(0);
		} else {
			return null;
		}
	}

	public void setGeofence(Geofence fence) {
		if (fence.isActive()) {
			logger.debug("Geofence of {} is set to lat {}, lng {}, radius {}",
					imei, fence.getLat(), fence.getLng(), fence.getRadius());
		} else {
			logger.debug("Geofence of {} is disabled.");
		}

		// TODO Auto-generated method stub

	}

	private void setupTemplates(GId agentGid) throws SDKException {
		ManagedObjectRepresentation source = new ManagedObjectRepresentation();
		source.setId(gid);
		source.setSelf(self);

		locationUpdate.setType(EVENT_TYPE);
		locationUpdate.setText("Location updated");
		locationUpdate.setSource(source);

		fenceAlarm.setType(ALARM_TYPE);
		fenceAlarm.setSeverity(CumulocitySeverities.MAJOR.toString());
		fenceAlarm.setText("Asset left preconfigured geo fence.");
		fenceAlarm.setSource(source);

		fenceAlarmFilter.bySource(source);
		fenceAlarmFilter.byStatus(CumulocityAlarmStatuses.ACTIVE);
	}

	private void createMo(GId agentGid) throws SDKException {
		ManagedObjectRepresentation device = new ManagedObjectRepresentation();

		SupportedOperations ops = ConnectionRegistry.instance().get(imei)
				.getSupportedOperations();
		device.set(ops);
		device.set(new IsDevice());

		ID extId = new ID(imei);
		extId.setType(XTID_TYPE);

		device.setType(TYPE);
		device.setName("Tracker " + imei);

		createOrUpdate(device, extId, agentGid);
		gid = device.getId();
		self = device.getSelf();
	}

	private Logger logger = LoggerFactory.getLogger(getClass());
	private EventApi events;
	private AlarmApi alarms;

	private String imei;
	private GId gid;
	private String self;

	private EventRepresentation locationUpdate = new EventRepresentation();
	private AlarmRepresentation fenceAlarm = new AlarmRepresentation();
	private AlarmFilter fenceAlarmFilter = new AlarmFilter();
}
