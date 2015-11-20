package c8y.trackeragent.utils;

import java.io.UnsupportedEncodingException;

import com.google.common.base.Joiner;
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
        String partsStr = stripReportSep(text);        
        Iterable<String> parts = Splitter.on(fieldSep).split(partsStr);
        return Iterables.toArray(parts, String.class);
    }

    private String stripReportSep(String partsStr) {
        if (partsStr.endsWith(reportSep)) {
            partsStr = partsStr.substring(0, partsStr.length() - 1);
        }
        return partsStr;
    }
    
    private static byte[] asBytes(String msg) {
        try {
            return msg.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public DeviceMessage append(DeviceMessage other) {
        String text = Joiner.on(reportSep).join(stripReportSep(this.text), stripReportSep(other.text));
        return new DeviceMessage(fieldSep, reportSep, text + reportSep);
    }

    @Override
    public String toString() {
        return text;
    }
}
