package c8y.trackeragent;

import java.io.OutputStream;
import java.util.Arrays;

public class ReportContext {
    
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
    
    public String getReportEntry(int index) {
        return (index < report.length) ? report[index] : null;
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
    

}
