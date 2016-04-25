package c8y.trackeragent.protocol.mt90g.parser;

import static org.fest.assertions.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

public class MT90GParserTest {

    
    @Test
    public void shouldCorrectlyCalculateBattery() throws Exception {
        MT90GParser mt90gParser = new MT90GParser(null,null);
        
        BigDecimal batteryVoltage = mt90gParser.getBattery("00D2|0000|0000|0ACB|0002");
        
        assertThat(batteryVoltage).isEqualTo(new BigDecimal("0.0209"));
    }
}
