package c8y.trackeragent.utils.message;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class TrackerMessage {
    
    private final LinkedList<Report> reports = new LinkedList<Report>();
    private final String fieldSep;
    private final String reportSep;
    private final String reportPrefix;
    
    public TrackerMessage(String fieldSep, String reportSep, String reportPrefix) {
        this.fieldSep = fieldSep;
        this.reportSep = reportSep;
        this.reportPrefix = reportPrefix;
    }

    public TrackerMessage(String fieldSep, String reportSep) {
        this(fieldSep, reportSep, "");
    }
    
    public byte[] asBytes() {
        return asBytes(asText());
    }
    
    public String asText() {
        return reportPrefix + Joiner.on(reportSep).join(reports) + reportSep;
    }
    
    public TrackerMessage fromText(String text) {
        text = stripPrefixAndLastReportSep(text);
        reports.clear();
        for (String reportStr : Splitter.on(reportSep).split(text)) {
            reports.add(new Report(reportStr));
        }
        return this;
    }

    private String stripPrefixAndLastReportSep(String text) {
        if (!reportPrefix.isEmpty() && text.startsWith(reportPrefix)) {
            text = text.substring(1);
        }
        if (text.endsWith(reportSep)) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }
    
    public String[] asArray() {
        List<String> parts = new ArrayList<String>();
        for(Report report : reports) {
            parts.addAll(report.getFields());
        }
        return Iterables.toArray(parts, String.class);
    }
    
    private static byte[] asBytes(String msg) {
        try {
            return msg.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public TrackerMessage appendField(int value) {
        return appendField("" + value);
    }
    
    public TrackerMessage appendField(BigDecimal value) {
        return appendField("" + value);
    }
    
    public TrackerMessage appendField(String text) {
        if (reports.isEmpty()) {
            reports.add(new Report());
        }
        Report report = reports.getLast();
        report.appendField(text);
        return this;
    }
    
    public LinkedList<Report> getReports() {
        return reports;
    }

    public TrackerMessage appendReport(TrackerMessage msg) {
        this.reports.addAll(msg.getReports());
        return this;
    }

    @Override
    public String toString() {
        return asText();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fieldSep == null) ? 0 : toString().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return this.toString().equals(obj.toString());
    }

    private class Report {
        
        private final List<String> fields = new ArrayList<String>();
        
        public Report() {
            super();
        }

        public Report(String reportStr) {
            fields.addAll(Splitter.on(fieldSep).splitToList(reportStr));
        }

        void appendField(String field) {
            fields.add(field);
        }

        @Override
        public String toString() {
            return Joiner.on(fieldSep).join(fields);
        }

        public List<String> getFields() {
            return fields;
        }
    }
    

    
}
