package c8y.trackeragent.utils;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class TK10xCoordinatesTranslatorTest {
    
    @Test
    public void shouldParseLatitude() throws Exception {
        double actual = TK10xCoordinatesTranslator.parseLatitude("5114.3471", "N");
        
        assertThat(actual).isEqualTo(51.2391183);
    }
    
    @Test
    public void shouldParseLongitude() throws Exception {
        double actual = TK10xCoordinatesTranslator.parseLongitude("00643.2373", "E");
        
        assertThat(actual).isEqualTo(6.7206217);
    }

}
