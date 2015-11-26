package c8y.trackeragent.utils.message;

import java.io.UnsupportedEncodingException;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class TrackerMessage {
    
    private final String text;
    private final String fieldSep;
    private final String reportSep;

    public TrackerMessage(String fieldSep, String reportSep, String text) {
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
        return strip(partsStr, reportSep);
    }
    
//    private String stripFieldSep(String partsStr) {
//        return strip(partsStr, fieldSep);
//    }
//    
//    private String stripSeps(String partsStr) {
//        return stripFieldSep(stripReportSep(partsStr));
//    }
    
    private static String strip(String source, String postfix) {
        if (source.endsWith(postfix)) {
            source = source.substring(0, source.length() - postfix.length());
        }
        return source;
        
    }
    
    private static byte[] asBytes(String msg) {
        try {
            return msg.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public TrackerMessage appendReport(TrackerMessage other) {
        String text = Joiner.on(reportSep).join(stripReportSep(this.text), stripReportSep(other.text));
        return new TrackerMessage(fieldSep, reportSep, text + reportSep);
    }
    
    public TrackerMessage appendField(TrackerMessage other) {
        String text = Joiner.on(fieldSep).join(stripReportSep(this.text), stripReportSep(other.text));
        return new TrackerMessage(fieldSep, reportSep, text);
    }

    @Override
    public String toString() {
        return text;
    }
}
