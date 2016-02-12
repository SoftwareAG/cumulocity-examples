package c8y.trackeragent.protocol.telic.parser;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public enum LogCodeType {
    
    TIME_EVENT("Time Event", "99"),
    DISTANCE_EVENT("Distance Event", "98"),
    ANGULAR_CHANGE_EVENT("Angular Change Event", "6"),
    POWER_EVENT("Power Event ", "1", "5");
    
    private final String label;
    private final Set<String> codes;

    private LogCodeType(String label, String... codes) {
        this.label = label;
        this.codes = ImmutableSet.<String>builder().add(codes).build();
    }
    
    public String getLabel() {
        return label;
    }
    
    public boolean match(String code) {
        return codes.contains(code);
    }
    
    

}
