package c8y.trackeragent.utils;

import java.util.ArrayList;
import java.util.List;

public class ArrayBuilder {
    
    private List<String> result = new ArrayList<String>();
    
    public static ArrayBuilder array() {
        return new ArrayBuilder();
    }
    
    public ArrayBuilder withValue(int index, Object value) {
        while(result.size() < index + 1) {
            result.add("");
        }
        result.set(index, value.toString());
        return this;
    }
    
    public String[] build() {
        for (int index = 0; index < result.size(); index++) {
            if (result.get(index) == null) {
                result.add(index, "");
            }
        }
        return result.toArray(new String[0]);
    }

}
