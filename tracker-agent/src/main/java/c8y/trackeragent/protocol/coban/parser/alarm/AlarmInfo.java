package c8y.trackeragent.protocol.coban.parser.alarm;

public class AlarmInfo {
    
    private final String type;
    private final String param;
    
    public AlarmInfo(String type) {
        this(type, null);
    }

    public AlarmInfo(String type, String param) {
        this.type = type;
        this.param = param;
    }

    public String getType() {
        return type;
    }

    public String getParam() {
        return param;
    }

    @Override
    public String toString() {
        return String.format("AlarmInfo [type=%s, param=%s]", type, param);
    }

}
