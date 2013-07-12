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

import c8y.Battery;
import c8y.Geofence;
import c8y.IsDevice;
import c8y.Mobile;
import c8y.MotionTracking;
import c8y.Position;
import c8y.SignalStrength;
import c8y.SupportedMeasurements;
import c8y.SupportedOperations;

import com.cumulocity.model.ID;
import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.model.event.CumulocitySeverities;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmCollectionRepresentation;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.PagedCollectionResource;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.alarm.AlarmFilter;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;

public class TrackerDevice extends DeviceManagedObject {
	public static final String TYPE = "c8y_Tracker";
	public static final String XTID_TYPE = "c8y_Imei";
	public static final String BAT_TYPE = "c8y_TrackerBattery";
	public static final String SIG_TYPE = "c8y_TrackerSignal";
	
	// TODO These should really come device-capabilities/sensor library.
	public static final String LU_EVENT_TYPE = "c8y_LocationUpdate";
	public static final String GEO_ALARM_TYPE = "c8y_GeofenceAlarm";
	public static final String MOT_ALARM_TYPE = "c8y_MotionAlarm";
	public static final String POWER_ALARM_TYPE = "c8y_PowerAlarm";

	public TrackerDevice(Platform platform, GId agentGid, String imei)
			throws SDKException {
		super(platform);
		this.events = platform.getEventApi();
		this.alarms = platform.getAlarmApi();
		this.measurements = platform.getMeasurementApi();

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

	public void setPosition(Position position) throws SDKException {
		logger.debug("Updating location of {} to lat {}, lng {}, alt {}", imei,
				position.getLat(), position.getLng(), position.getAlt());

		ManagedObjectRepresentation device = new ManagedObjectRepresentation();
		device.set(position);
		getInventory().getManagedObject(gid).update(device);

		locationUpdate.setTime(new Date());
		locationUpdate.set(position);
		events.create(locationUpdate);
	}

	public void setGeofence(Geofence fence) throws SDKException {
		if (fence.isActive()) {
			logger.debug("Geofence of {} is set to lat {}, lng {}, radius {}",
					imei, fence.getLat(), fence.getLng(), fence.getRadius());
		} else {
			logger.debug("Geofence of {} is disabled.");
		}

		ManagedObjectRepresentation device = new ManagedObjectRepresentation();
		device.set(fence);
		getInventory().getManagedObject(gid).update(device);
	}

	public void geofenceAlarm(boolean raise) throws SDKException {
		logger.debug("{} {} the geofence", imei, raise ? "left" : "entered");
		createOrCancelAlarm(raise, fenceAlarm);
	}

	public void setMotionTracking(boolean active) throws SDKException {
		logger.debug("Motion tracking for {} set to {}", imei, active);

		ManagedObjectRepresentation device = new ManagedObjectRepresentation();
		MotionTracking motion = new MotionTracking();
		motion.setActive(active);
		device.set(motion);
		getInventory().getManagedObject(gid).update(device);
	}

	public void motionAlarm(boolean moving) throws SDKException {
		logger.debug("{} {}", imei, moving ? "is moving" : "stopped moving");
		createOrCancelAlarm(moving, motionAlarm);
	}

	public void powerAlarm(boolean powerLost,boolean extInt) throws SDKException {
		logger.debug("{} {}", imei, powerLost ? "lost power" : "has power again");
		powerAlarm.setText(extInt ? "Asset lost power" : "Tracker lost power");
		createOrCancelAlarm(powerLost, powerAlarm);
	}
	
	public void batteryLevel(int level) throws SDKException {
		logger.debug("Battery level for {} is at {}", imei, level);
		battery.setLevel(new BigDecimal(level));
		batteryMsrmt.setTime(new Date());
		measurements.create(batteryMsrmt);
	}
	
	public void signalStrength(BigDecimal rssi, BigDecimal ber) throws SDKException {
		logger.debug("Signal strength for {} is {}, BER is {}", imei, rssi, ber);
		if (rssi != null) {
			gprsSignal.setRssi(rssi);
		}
		if (ber != null) {
			gprsSignal.setBer(ber);
		}
		gprsSignalMsrmt.setTime(new Date());
		measurements.create(gprsSignalMsrmt);
	}
	
	public void setCellId(String cellId) throws SDKException {
		ManagedObjectRepresentation device = new ManagedObjectRepresentation();
		mobile.setCellId(cellId);
		device.set(mobile);
		getInventory().getManagedObject(gid).update(device);		
	}

	private void createOrCancelAlarm(boolean status,
			AlarmRepresentation newAlarm) throws SDKException {
		String newStatus = status ? CumulocityAlarmStatuses.ACTIVE.toString()
				: CumulocityAlarmStatuses.CLEARED.toString();

		AlarmRepresentation activeAlarm = findActiveAlarm(GEO_ALARM_TYPE);

		if (activeAlarm != null) {
			activeAlarm.setTime(new Date());
			activeAlarm.setStatus(newStatus);
			alarms.updateAlarm(activeAlarm);
		} else {
			newAlarm.setTime(new Date());
			newAlarm.setStatus(newStatus);
			alarms.create(newAlarm);
		}
	}

	private AlarmRepresentation findActiveAlarm(String type)
			throws SDKException {
		PagedCollectionResource<AlarmCollectionRepresentation> alarmQuery = alarms
				.getAlarmsByFilter(alarmFilter);

		// TODO This is a bit of a shortcut.
		AlarmCollectionRepresentation acr = alarmQuery.get(1000);
		List<AlarmRepresentation> alarms = acr.getAlarms();

		for (AlarmRepresentation alarm : alarms) {
			if (type.equals(alarm.getType())) {
				return alarm;
			}
		}
		return null;
	}

	private void setupTemplates(GId agentGid) throws SDKException {
		ManagedObjectRepresentation source = new ManagedObjectRepresentation();
		source.setId(gid);
		source.setSelf(self);

		locationUpdate.setType(LU_EVENT_TYPE);
		locationUpdate.setText("Location updated");
		locationUpdate.setSource(source);

		fenceAlarm.setType(GEO_ALARM_TYPE);
		fenceAlarm.setSeverity(CumulocitySeverities.MAJOR.toString());
		fenceAlarm.setText("Asset left geo fence.");
		fenceAlarm.setSource(source);

		motionAlarm.setType(MOT_ALARM_TYPE);
		motionAlarm.setSeverity(CumulocitySeverities.MINOR.toString());
		motionAlarm.setText("Asset was moved.");
		motionAlarm.setSource(source);

		powerAlarm.setType(POWER_ALARM_TYPE);
		powerAlarm.setSeverity(CumulocitySeverities.MAJOR.toString());
		powerAlarm.setText("Asset lost power.");
		powerAlarm.setSource(source);

		alarmFilter.bySource(source);
		alarmFilter.byStatus(CumulocityAlarmStatuses.ACTIVE);
		
		batteryMsrmt.setType(BAT_TYPE);
		batteryMsrmt.set(battery);
		batteryMsrmt.setSource(source);
		
		gprsSignalMsrmt.setType(SIG_TYPE);
		gprsSignalMsrmt.set(gprsSignal);
		gprsSignalMsrmt.setSource(source);
	}

	private void createMo(GId agentGid) throws SDKException {
		ManagedObjectRepresentation device = new ManagedObjectRepresentation();

		Executor exec = ConnectionRegistry.instance().get(imei);
		if (exec != null) {
			SupportedOperations ops = exec.getSupportedOperations();
			device.set(ops);			
		}

		SupportedMeasurements msmts = new SupportedMeasurements();
		msmts.add("c8y_Battery");
		msmts.add("c8y_SignalStrength");
		device.set(msmts);

		device.set(new IsDevice());
		
		mobile = new Mobile();
		mobile.setImei(imei);
		device.set(mobile);

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
	private MeasurementApi measurements;

	private String imei;
	private GId gid;
	private String self;
	private Mobile mobile;

	private EventRepresentation locationUpdate = new EventRepresentation();
	
	private AlarmRepresentation fenceAlarm = new AlarmRepresentation();
	private AlarmRepresentation motionAlarm = new AlarmRepresentation();
	private AlarmRepresentation powerAlarm = new AlarmRepresentation();
	private AlarmFilter alarmFilter = new AlarmFilter();
	
	private MeasurementRepresentation batteryMsrmt = new MeasurementRepresentation();
	private Battery battery = new Battery();
	private MeasurementRepresentation gprsSignalMsrmt = new MeasurementRepresentation();
	private SignalStrength gprsSignal = new SignalStrength();
}
