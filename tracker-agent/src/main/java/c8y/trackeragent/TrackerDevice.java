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

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.ID;
import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.model.event.CumulocitySeverities;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.alarm.AlarmFilter;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.event.EventFilter;
import com.cumulocity.sdk.client.measurement.MeasurementApi;

import c8y.Battery;
import c8y.Configuration;
import c8y.Geofence;
import c8y.IsDevice;
import c8y.Mobile;
import c8y.MotionTracking;
import c8y.Position;
import c8y.RFV16Config;
import c8y.RequiredAvailability;
import c8y.Restart;
import c8y.SignalStrength;
import c8y.SupportedOperations;
import c8y.trackeragent.protocol.coban.device.CobanDevice;
import c8y.trackeragent.protocol.coban.device.CobanDeviceFactory;
import c8y.trackeragent.utils.TrackerConfiguration;

public class TrackerDevice extends DeviceManagedObject {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    public static final String TYPE = "c8y_Tracker";
    public static final String XTID_TYPE = "c8y_Imei";
    public static final String VIN_XT_TYPE = "c8y_VIN";
    public static final String BAT_TYPE = "c8y_TrackerBattery";
    public static final String SIG_TYPE = "c8y_TrackerSignal";

    // TODO These should really come device-capabilities/sensor library.
    public static final String LU_EVENT_TYPE = "c8y_LocationUpdate";
    public static final String GEO_ALARM_TYPE = "c8y_GeofenceAlarm";
    public static final String MOTION_DETECTED_EVENT_TYPE = "c8y_MotionEvent";
    public static final String MOTION_ENDED_EVENT_TYPE = "c8y_MotionEndedEvent";
    public static final String POWER_ALARM_TYPE = "c8y_PowerAlarm";

    private EventApi events;
    private AlarmApi alarms;
    private MeasurementApi measurements;
    private DeviceControlApi deviceControl;

    private String imei;
    private GId gid;
    private String self;
    private Mobile mobile;

    private EventRepresentation eventMotionDetected = new EventRepresentation();
    private EventRepresentation eventMotionEnded = new EventRepresentation();

    private AlarmRepresentation fenceAlarm = new AlarmRepresentation();
    private AlarmRepresentation powerAlarm = new AlarmRepresentation();
    private AlarmFilter alarmFilter = new AlarmFilter();
    private EventFilter eventFilter = new EventFilter();

    private MeasurementRepresentation batteryMsrmt = new MeasurementRepresentation();
    private Battery battery = new Battery();
    private MeasurementRepresentation gprsSignalMsrmt = new MeasurementRepresentation();
    private SignalStrength gprsSignal = new SignalStrength();
    private TrackerConfiguration trackerConfig;

    public TrackerDevice(TrackerPlatform platform, TrackerConfiguration trackerConfig, GId agentGid, String imei) throws SDKException {
        super(platform);
        this.trackerConfig = trackerConfig;
        this.events = platform.getEventApi();
        this.alarms = platform.getAlarmApi();
        this.measurements = platform.getMeasurementApi();
        this.deviceControl = platform.getDeviceControlApi();

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
        EventRepresentation event = aLocationUpdateEvent();
        setPosition(event, position);
    }
    
    public void setPosition(EventRepresentation event, Position position) {
        ManagedObjectRepresentation device = aDevice();        
        logger.debug("Updating location of {} to {}.", imei, position);
        device.set(position);
        event.set(position);
        getInventory().update(device);        
        events.create(event);
    }
    
    private ManagedObjectRepresentation aDevice() {
        ManagedObjectRepresentation representation = new ManagedObjectRepresentation();
        representation.setId(gid);
        return representation;
    }

    public Position getPosition() {
        ManagedObjectRepresentation device = getManagedObject();
        return device == null ? null : device.get(Position.class); 
    }
    
    public ManagedObjectRepresentation getManagedObject() {
        ManagedObjectRepresentation mo = inventory.get(gid);
        if(mo == null) {
            throw new RuntimeException("No device for id " + gid);
        }
        return mo;
    }

    public void setGeofence(Geofence fence) throws SDKException {
        if (fence.isActive()) {
            logger.debug("Geofence of {} is set to lat {}, lng {}, radius {}", imei, fence.getLat(), fence.getLng(), fence.getRadius());
        } else {
            logger.debug("Geofence of {} is disabled.");
        }

        ManagedObjectRepresentation device = new ManagedObjectRepresentation();
        device.set(fence);
        device.setId(gid);
        getInventory().update(device);
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
        device.setId(gid);
        getInventory().update(device);
    }

    public void motionEvent(boolean moving) throws SDKException {
    	if(moving){
    		eventMotionDetected.setTime(new Date());
    		events.create(eventMotionDetected);
    		logger.debug("{} is moving", imei);
    	} else {
    		eventMotionEnded.setTime(new Date());
    		events.create(eventMotionEnded);
    		logger.debug("{} stopped moving", imei);
    	}
    }

    public void powerAlarm(boolean powerLost, boolean external) throws SDKException {
        logger.debug("{} {}", imei, powerLost ? "lost power" : "has power again");
        String msg = external ? "Asset " : "Tracker ";
        msg += powerLost ? "lost power" : "has power again";
        powerAlarm.setText(msg);
        createOrCancelAlarm(powerLost, powerAlarm);
    }

    public void batteryLevel(int level) throws SDKException {
        logger.debug("Battery level for {} is at {}", imei, level);
        battery.setLevelValue(new BigDecimal(level));
        batteryMsrmt.setTime(new Date());
        measurements.create(batteryMsrmt);
    }

    public void signalStrength(BigDecimal rssi, BigDecimal ber) throws SDKException {
        logger.debug("Signal strength for {} is {}, BER is {}", imei, rssi, ber);
        if (rssi != null) {
            gprsSignal.setRssiValue(rssi);
        }
        if (ber != null) {
            gprsSignal.setBerValue(ber);
        }
        gprsSignalMsrmt.setTime(new Date());
        measurements.create(gprsSignalMsrmt);
    }

    public void setCellId(String cellId) throws SDKException {
        ManagedObjectRepresentation device = new ManagedObjectRepresentation();
        mobile.setCellId(cellId);
        device.set(mobile);
        device.setId(gid);
        getInventory().update(device);
    }
    
    public void ping() throws SDKException {
        logger.info("Ping to device with id {}.", gid);
        ManagedObjectRepresentation device = new ManagedObjectRepresentation();
        device.setId(gid);
        getInventory().update(device);
    }
    
    public AlarmRepresentation createAlarm(AlarmRepresentation newAlarm) throws SDKException { 
        return createOrCancelAlarm(true, newAlarm);
    }
    
    public AlarmRepresentation clearAlarm(AlarmRepresentation newAlarm) throws SDKException { 
        return createOrCancelAlarm(false, newAlarm);
    }

    private AlarmRepresentation createOrCancelAlarm(boolean status, AlarmRepresentation newAlarm) throws SDKException {
        newAlarm.setSource(asSource());
        String newStatus = status ? CumulocityAlarmStatuses.ACTIVE.toString() : CumulocityAlarmStatuses.CLEARED.toString();

        AlarmRepresentation activeAlarm = findActiveAlarm(newAlarm.getType());

        if (activeAlarm != null) {
            activeAlarm.setTime(new Date());
            activeAlarm.setStatus(newStatus);
            return alarms.update(activeAlarm);
        } else {
            newAlarm.setTime(new Date());
            newAlarm.setStatus(newStatus);
            return alarms.create(newAlarm);
        }
    }
    
    public AlarmRepresentation findActiveAlarm(String type) throws SDKException {
        for (AlarmRepresentation alarm : alarms.getAlarmsByFilter(alarmFilter).get().allPages()) {
            if (type.equals(alarm.getType())) {
                return alarm;
            }
        }
        return null;
    }
    
    public EventRepresentation findLastEvent(String type) throws SDKException {
        for (EventRepresentation event : events.getEventsByFilter(eventFilter).get().allPages()) {
            if (type.equals(event.getType())) {
                return event;
            }
        }
        return null;
    }
    
    public EventRepresentation aLocationUpdateEvent() {
        EventRepresentation locationUpdate = new EventRepresentation();
        ManagedObjectRepresentation source = asSource();
        
        locationUpdate.setType(LU_EVENT_TYPE);
        locationUpdate.setText("Location updated");
        locationUpdate.setSource(source);
        locationUpdate.setTime(new Date());
        return locationUpdate;
    }

    private void setupTemplates(GId agentGid) throws SDKException {
        ManagedObjectRepresentation source = asSource();

        fenceAlarm.setType(GEO_ALARM_TYPE);
        fenceAlarm.setSeverity(CumulocitySeverities.MAJOR.toString());
        fenceAlarm.setText("Asset left geo fence.");
        fenceAlarm.setSource(source);

        eventMotionDetected.setSource(source);
        eventMotionDetected.setType(MOTION_DETECTED_EVENT_TYPE);
        eventMotionDetected.setText("Motion detected");
        
        eventMotionEnded.setSource(source);
        eventMotionEnded.setType(MOTION_ENDED_EVENT_TYPE);
        eventMotionEnded.setText("Motion ended");
        
        powerAlarm.setType(POWER_ALARM_TYPE);
        powerAlarm.setSeverity(CumulocitySeverities.MAJOR.toString());
        powerAlarm.setText("Asset lost power.");
        powerAlarm.setSource(source);

        alarmFilter.bySource(source.getId());
        alarmFilter.byStatus(CumulocityAlarmStatuses.ACTIVE);
        
        eventFilter.bySource(source.getId());

        batteryMsrmt.setType(BAT_TYPE);
        batteryMsrmt.set(battery);
        batteryMsrmt.setSource(source);

        gprsSignalMsrmt.setType(SIG_TYPE);
        gprsSignalMsrmt.set(gprsSignal);
        gprsSignalMsrmt.setSource(source);
    }

    private ManagedObjectRepresentation asSource() {
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(gid);
        source.setSelf(self);
        return source;
    }

    private void createMo(GId agentGid) throws SDKException {
        ManagedObjectRepresentation device = new ManagedObjectRepresentation();

        RequiredAvailability availability = new RequiredAvailability(15);
        device.set(availability);
        
        SupportedOperations ops = new SupportedOperations();
        ops.add("c8y_Restart");
        ops.add("c8y_Configuration");
        ops.add("c8y_MotionTracking");
        ops.add("c8y_Geofence");
        ops.add("c8y_LogfileRequest");
        device.set(ops);
        
        device.set(Arrays.asList("agentlog"), "c8y_SupportedLogs");

        device.set(new MotionTracking());
        device.set(new IsDevice());
        device.set(new Configuration());
        device.set(new Restart());

        mobile = new Mobile();
        mobile.setImei(imei);
        device.set(mobile);

        ID extId = imeiAsId(imei);

        device.setType(TYPE);
        device.setName("Tracker " + imei);

        createOrUpdate(device, extId, agentGid);
        gid = device.getId();
        self = device.getSelf();
    }

    public void registerVIN(String vin) {
        if (isEmpty(vin) || gid == null) {
            return;
        }
        ID extId = asVIN(vin);
        try {
            if (tryGetBinding(extId) == null) {
                ManagedObjectRepresentation mor = new ManagedObjectRepresentation();
                mor.setId(gid);
                bind(mor, extId);
            }
        } catch (SDKException e) {
            logger.error("Failed to register vin for device " + gid, e);
        }
    }

    private ID asVIN(String vin) {
        ID extId = new ID(vin);
        extId.setType(TrackerDevice.VIN_XT_TYPE);
        return extId;
    }
    
    public void createMeasurement(MeasurementRepresentation measurement) {
        logger.debug("Create measurement {} for device {}", measurement, imei);
        measurements.create(measurement);
    }

    public CobanDevice getCobanDevice() {
        ManagedObjectRepresentation mo = getManagedObject();
        CobanDevice cobanDevice = new CobanDeviceFactory(trackerConfig, mo).create();
        logger.info("Received coban device config: {} for imei: {}", cobanDevice, imei);
        return cobanDevice;
    }
    
    public void set(RFV16Config newDeviceConfig) {
        logger.info("Update {} for imei: {}", newDeviceConfig, imei);
        ManagedObjectRepresentation device = aDevice();
        device.set(newDeviceConfig);
        getInventory().update(device);        
    }
    
    public RFV16Config getRFV16Config() {
        RFV16Config result = getManagedObject().get(RFV16Config.class);
        return result == null ? new RFV16Config() : result;
    }
    
    public void updateMobile(Mobile mobile) {
        ManagedObjectRepresentation device = aDevice();        
        logger.debug("Updating mobile of {} to {}.", imei, mobile);
        device.set(mobile);
        getInventory().update(device);        
    }
    
    public void setOperationSuccessful(OperationRepresentation operation) {
        operation.setStatus(OperationStatus.SUCCESSFUL.toString());
        deviceControl.update(operation);
    }

}
