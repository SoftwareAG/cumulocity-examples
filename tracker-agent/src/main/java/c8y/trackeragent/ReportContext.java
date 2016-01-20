package c8y.trackeragent;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.utils.message.TrackerMessage;

public class ReportContext {
    
    private static Logger logger = LoggerFactory.getLogger(ReportContext.class);
    
    private final String[] report;
    private final String imei;
    private final OutputStream out;
    private final Map<String, Object> connectionParams;
    
    public ReportContext(String[] report, String imei, OutputStream out) {
        this(report, imei, out, new HashMap<String, Object>());
    }

    public ReportContext(String[] report, String imei, OutputStream out, Map<String, Object> connectionParams) {
        this.report = report;
        this.imei = imei;
        this.out = out;
        this.connectionParams = connectionParams;
    }

    public String[] getReport() {
        return report;
    }

    public String getImei() {
        return imei;
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

    public Object getConnectionParam(String paramName) {
        return connectionParams.get(paramName);
    }
    
    public boolean isConnectionFlagOn(String paramName) {
        return Boolean.TRUE.equals(getConnectionParam(paramName));
    }
    
    public void setConnectionParam(String paramName, Object paramValue) {
        connectionParams.put(paramName, paramValue);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((imei == null) ? 0 : imei.hashCode());
        result = prime * result + Arrays.hashCode(report);
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
        ReportContext other = (ReportContext) obj;
        if (imei == null) {
            if (other.imei != null)
                return false;
        } else if (!imei.equals(other.imei))
            return false;
        if (!Arrays.equals(report, other.report))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("ReportContext [report=%s, imei=%s]", Arrays.toString(report), imei);
    }

}
