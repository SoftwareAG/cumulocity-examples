package c8y.trackeragent.protocol.telic.parser;

public enum FixType {

    _1D("1", "No Fix"), 
    _2D("2", "2D Fix"), 
    _3D("3", "3D Fix");

    private final String value;

    private final String label;

    private FixType(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public static FixType forValue(String value) {
        for (FixType fixType : FixType.values()) {
            if (fixType.getValue().equals(value)) {
                return fixType;
            }
        }
        return null;
    }

}
