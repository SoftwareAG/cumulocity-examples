package c8y.trackeragent.protocol.telic.parser;

public enum LogCodeType {
    
    TIME_EVENT("Time Event"),
    DISTANCE_EVENT("Distance Event"),
    ANGULAR_CHANGE_EVENT("Angular Change Event"),
    POWER_EVENT("Power Event ");
    
    private final String label;

    private LogCodeType(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
    
    

}
