package c8y.trackeragent.protocol.telic.parser;

public enum LogCodeType {
    
    TIME_EVENT("Time Event", "99"),
    DISTANCE_EVENT("Distance Event", "98"),
    ANGULAR_CHANGE_EVENT("Angular Change Event", "6"),
    POWER_EVENT_ON("Power Event", "1"),
    POWER_EVENT_OF("Power Event", "5"),
    GEOFENCE_ENTER("Geofence Area Enter", "7"),
    GEOFENCE_EXIT("Geofence Area Exit", "8"),
    MOTION_SENSOR_MOTION("Motion Start", "25"),
    MOTION_SENSOR_STATIONARY("Motion Stop", "26");
    
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
