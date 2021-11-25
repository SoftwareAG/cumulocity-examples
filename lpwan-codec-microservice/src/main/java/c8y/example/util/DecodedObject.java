package c8y.example.util;

import java.util.HashMap;
import java.util.Map;

public class DecodedObject {
    
    private Map<String, Object> fields = new HashMap<>();

    public void putValue(Object value) {
        fields.put("value", value);
    }
    
    public void putUnit(Object unit) {
        fields.put("unit", unit);
    }
    
    public Object getValue() {
        return fields.get("value");
    }
    
    public Object getUnit() {
        return fields.get("unit");
    }
    
    public Object getFields() {
        if (getUnit() == null) {
            return getValue();
        } else {
            return fields;
        }
    }

}
