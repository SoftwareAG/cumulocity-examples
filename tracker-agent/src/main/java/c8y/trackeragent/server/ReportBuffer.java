package c8y.trackeragent.server;

import java.nio.charset.Charset;

public class ReportBuffer {

    static final Charset CHARSET = Charset.forName("US-ASCII");
    
    private StringBuilder content = new StringBuilder();
    private final String reportSeparator;
    private final Object monitor = new Object();
    
    public ReportBuffer(String reportSeparator) {
        this.reportSeparator = reportSeparator;
    }

    public void append(byte[] data, int dataLength) {
        synchronized (monitor) {
            if (data == null) {
                return;
            }
            byte[] dataCopy = copy(data, dataLength);
            content = content.append(new String(dataCopy, CHARSET));            
        }
    }

    public String getReport() {
        synchronized (monitor) {
            int indexOfSep = content.indexOf(reportSeparator);
            if (indexOfSep >= 0) {
                String result = content.substring(0, indexOfSep);
                content = new StringBuilder(content.substring(indexOfSep + 1));
                return result;
            } else {
                return null;
            }
        }
    }
    
    private static byte[] copy(byte[] data, int dataLength) {
        byte[] dataCopy = new byte[dataLength];
        System.arraycopy(data, 0, dataCopy, 0, dataLength);
        return dataCopy;
    }


}
