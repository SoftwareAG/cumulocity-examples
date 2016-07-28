package c8y.trackeragent.server;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import c8y.trackeragent.server.ReportBuffer;

public class ReportBufferTest {
    
    private static final String REPORT_SEPARATOR = ";";
    
    ReportBuffer reportBuffer = new ReportBuffer(REPORT_SEPARATOR);
    
    @Test
    public void shouldParseReport() throws Exception {
        assertThat(reportBuffer.getReport()).isNull();
        
        append("abc");
        assertThat(reportBuffer.getReport()).isNull();
        
        append(REPORT_SEPARATOR);
        assertThat(reportBuffer.getReport()).isEqualTo("abc");
        
        assertThat(reportBuffer.getReport()).isNull();
        
        append(REPORT_SEPARATOR);
        assertThat(reportBuffer.getReport()).isEmpty();
        
        append("123;");
        assertThat(reportBuffer.getReport()).isEqualTo("123");
    }

    private void append(String string) {
        byte[] bytes = string.getBytes(ReportBuffer.CHARSET);
        reportBuffer.append(bytes, bytes.length);
        
    }

}
