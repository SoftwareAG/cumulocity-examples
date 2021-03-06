/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.utils.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

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
