package c8y.trackeragent.utils.message;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class TrackerMessageTest {

    @Test
    public void shouldAppendReport() throws Exception {
        TrackerMessage rep0 = new TrackerMessage(",", ";", "sth00,sth01;");
        TrackerMessage rep1 = new TrackerMessage(",", ";", "sth10,sth11;");
        
        TrackerMessage rep01 = rep0.appendReport(rep1);
        
        assertThat(rep01.asText()).isEqualTo("sth00,sth01;sth10,sth11;");
    }
    
    @Test
    public void shouldAppendField() throws Exception {
        TrackerMessage rep0 = new TrackerMessage(",", ";", "sth00,sth01;");
        TrackerMessage rep1 = new TrackerMessage(",", ";", "sth10,sth11;");
        
        TrackerMessage rep01 = rep0.appendField(rep1);
        
        assertThat(rep01.asText()).isEqualTo("sth00,sth01,sth10,sth11");
    }
    
}
