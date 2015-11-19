package c8y.trackeragent.utils;

import java.io.UnsupportedEncodingException;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class DeviceMessage {
    
    private final String text;
    private final String fieldSep;
    private final String reportSep;

    public DeviceMessage(String fieldSep, String reportSep, String text) {
        this.fieldSep = fieldSep;
        this.reportSep = reportSep;
        this.text = text;
    }
    
    public byte[] asBytes() {
        return asBytes(text);
    }
    
    public String asText() {
        return text;
    }
    
    public String[] asArray() {
        String partsStr = text;
        if (partsStr.endsWith(reportSep)) {
            partsStr = partsStr.substring(0, partsStr.length() - 1);
        }        
        Iterable<String> parts = Splitter.on(fieldSep).split(text);
        return Iterables.toArray(parts, String.class);
    }
    
    private static byte[] asBytes(String msg) {
        try {
            return msg.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    

}
