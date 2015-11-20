package c8y.trackeragent;

import java.io.OutputStream;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportContext {
    
    private static Logger logger = LoggerFactory.getLogger(ReportContext.class);
    
    private final String[] report;
    private final String imei;
    private final OutputStream out;
    
    public ReportContext(String[] report, String imei, OutputStream out) {
        this.report = report;
        this.imei = imei;
        this.out = out;
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
    
    public int getNumberOfEntries() {
        return report.length;
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
