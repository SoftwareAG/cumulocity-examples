package c8y.trackeragent.server;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import c8y.trackeragent.server.DataBuffer;

public class DataBufferTest {
    
    private static final String REPORT_SEPARATOR = ";";
    
    DataBuffer dataBuffer = new DataBuffer(REPORT_SEPARATOR);
    
    @Test
    public void shouldParseReport() throws Exception {
        assertThat(dataBuffer.getReport()).isNull();
        
        append("abc");
        assertThat(dataBuffer.getReport()).isNull();
        
        append(REPORT_SEPARATOR);
        assertThat(dataBuffer.getReport()).isEqualTo("abc");
        
        assertThat(dataBuffer.getReport()).isNull();
        
        append(REPORT_SEPARATOR);
        assertThat(dataBuffer.getReport()).isEmpty();
        
        append("123;");
        assertThat(dataBuffer.getReport()).isEqualTo("123");
    }

    private void append(String string) {
        byte[] bytes = string.getBytes(DataBuffer.CHARSET);
        dataBuffer.append(bytes, bytes.length);
        
    }

}
