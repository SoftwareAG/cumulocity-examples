/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.telic.parser;

public enum LogCodeType {
    
    POWER_EVENT_ON("Power ON Event", "1"),
    EMERGENCY("Emergency", "2"),
    POSITION_LOCK_ALARM("Position Lock Alarm", "3"), 
    ALARM_TRACKING("Alarm Tracking", "4"),
    POWER_EVENT_OFF("Power OFF Event", "5"),
    ANGULAR_CHANGE_EVENT("Angular Change Event", "6"),
    GEOFENCE_ENTER("Geofence Area Enter", "7"),
    GEOFENCE_EXIT("Geofence Area Exit", "8"),
    GPS_FIX_LOST("GPS Fix Lost", "9"),
    PERIODIC_WAKEUP("Periodic Wakeup", "10"),
    MOTION_SENSOR_MOTION("Motion Start", "25"),
    MOTION_SENSOR_STATIONARY("Motion Stop", "26"),
    INCOMING_CALL_EVENT("Incoming Call Event", "30"),
    HEARTBEAT_EVENT("Heartbeat Event", "32"),
    DISTANCE_EVENT("Distance Event", "98"),
    TIME_EVENT("Time Event", "99");
    
    private final String label;
    private final String code;

    private LogCodeType(String label, String code) {
        this.label = label;
        this.code = code;
    }
    
    public String getLabel() {
        return label;
    }
    
    public boolean match(String code) {
        return this.code.equals(code);
    }
    
    public String getCode() {
        return code;
    }
    
    

}
