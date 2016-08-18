package c8y.trackeragent.server;

import static c8y.trackeragent.utils.ByteHelper.getBytes;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class ReportBufferTest {
    
    ReportBuffer reportBuffer = new ReportBuffer();
    
    @Test
    public void shouldParseReport() throws Exception {
        assertThat(reportBuffer.pollReport()).isNull();
        
        append("abc");
        assertThat(reportBuffer.pollReport()).isEqualTo(getBytes("abc"));
        
        assertThat(reportBuffer.pollReport()).isNull();
        
        append("abc", 1);
        assertThat(reportBuffer.pollReport()).isEqualTo(getBytes("a"));
    }

    private void append(String text) {
        byte[] bytes = getBytes(text);
        reportBuffer.append(bytes, bytes.length);
    }
    
    private void append(String text, int length) {
        byte[] bytes = getBytes(text);
        reportBuffer.append(bytes, length);
    }

}
