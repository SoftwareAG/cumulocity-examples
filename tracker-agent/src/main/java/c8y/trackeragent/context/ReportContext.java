package c8y.trackeragent.context;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.utils.message.TrackerMessage;

public class ReportContext extends ConnectionContext {
    
    private static Logger logger = LoggerFactory.getLogger(ReportContext.class);
    
    private final String[] report;
    private final OutputStream out;
    
    public ReportContext(String[] report, String imei, OutputStream out) {
        this(report, imei, out, new HashMap<String, Object>());
    }

    public ReportContext(String[] report, String imei, OutputStream out, Map<String, Object> connectionParams) {
    	super(imei, connectionParams);
        this.report = report;
        this.out = out;
    }

    public String[] getReport() {
        return report;
    }

    public OutputStream getOut() {
        return out;
    }
    
    public String getEntry(int index) {
        if (index < report.length) {
            return report[index];
        } else {
            logger.debug("There is no entry at index " + index + " for array " + Arrays.toString(report));
            return null;
        }
    }
    
    public BigDecimal getEntryAsNumber(int index) {
        String entry = getEntry(index);
        return StringUtils.isBlank(entry) ? null : new BigDecimal(entry.trim());
    }
    
    public Integer getEntryAsInt(int index) {
        String entry = getEntry(index);
        return StringUtils.isBlank(entry) ? null : Integer.parseInt(entry.trim());
    }
    
    public int getNumberOfEntries() {
        return report.length;
    }
    
    public void writeOut(TrackerMessage msg) {
        try {
            String text = msg.asText();
            logger.debug("Write to device: {}.", text);
            out.write(text.getBytes("US-ASCII"));
            out.flush();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    


}
