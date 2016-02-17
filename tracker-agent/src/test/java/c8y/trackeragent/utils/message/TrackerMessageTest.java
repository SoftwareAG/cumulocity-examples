package c8y.trackeragent.utils.message;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import c8y.trackeragent.context.ReportContext;

public class TrackerMessageTest {
    
    TrackerMessage msg1 = new TrackerMessage(",", ";");
    TrackerMessage msg2 = new TrackerMessage(",", ";");
    
    @Test
    public void shouldAppendField() throws Exception {
        msg1.appendField("a").appendField("b");
        
        assertThat(msg1.asText()).isEqualTo("a,b;");
    }

    @Test
    public void shouldAppendReport() throws Exception {
        msg1.appendField("a").appendField("b");
        msg2.appendField("c").appendField("d");
        
        msg1.appendReport(msg2);
        
        assertThat(msg1.asText()).isEqualTo("a,b;c,d;");
    }
    
    @Test
    public void shouldCreateReportFromString() throws Exception {
        msg1.appendField("a").appendField("b");
        
        TrackerMessage msg1Copy = new TrackerMessage(",", ";").fromText(msg1.asText());
        assertThat(msg1Copy.asText()).isEqualTo(msg1.asText());
    }
    
    @Test
    public void shouldCreateReportFromCobanReportString() throws Exception {
        TrackerMessage msg = new TrackerMessage(",", ";").fromText("imei:359710047377578,tracker,151211150247,,L,,,9707,,2660,,,");
        ReportContext ctx = new ReportContext(msg.asArray(), "1212223", null);
        
        System.out.println(ctx);
    }
    
    @Test
    public void shouldCreateReportWithPrefix() throws Exception {
        TrackerMessage msg1 = new TrackerMessage(",", ";", "*").appendField("A").appendField("B").appendField("C");
        TrackerMessage msg2 = new TrackerMessage(",", ";", "*").appendField("D").appendField("E").appendField("F");
        msg1.appendReport(msg2);
        
        assertThat(msg1.asText()).isEqualTo("*A,B,C;*D,E,F;");
    }
    
    @Test
    public void shouldParseReportWithPrefixFromText() throws Exception {
        TrackerMessage msg = new TrackerMessage(",", ";", "*").fromText("*A,B,C;*D,E,F;");
        
        assertThat(msg.getReports()).hasSize(2);
        assertThat(msg.toString()).isEqualTo("*A,B,C;*D,E,F;");
    }
    
    
}
