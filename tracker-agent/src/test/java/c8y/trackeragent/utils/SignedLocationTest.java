package c8y.trackeragent.utils;

import static c8y.trackeragent.utils.SignedLocation.latitude;
import static c8y.trackeragent.utils.SignedLocation.longitude;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

public class SignedLocationTest {
    
    private final static BigDecimal SEVEN = new BigDecimal(7);
    
    @Test
    public void shouldConvertLatitude() throws Exception {
        assertThat(latitude().withValue(SEVEN).getSymbol()).isEqualTo("N");
        assertThat(latitude().withValue(SEVEN.negate()).getSymbol()).isEqualTo("S");
        assertThat(latitude().withValue(SEVEN).getAbsValue()).isEqualTo("7");
        
        assertThat(latitude().withValue("7", "N").getValue()).isEqualTo(SEVEN);
        assertThat(latitude().withValue("7", "S").getValue()).isEqualTo(SEVEN.negate());
    }
    
    @Test
    public void shouldConvertLongitude() throws Exception {
        assertThat(longitude().withValue(SEVEN).getSymbol()).isEqualTo("E");
        assertThat(longitude().withValue(SEVEN.negate()).getSymbol()).isEqualTo("W");
        assertThat(longitude().withValue(SEVEN).getAbsValue()).isEqualTo("7");
        
        assertThat(longitude().withValue("7", "E").getValue()).isEqualTo(SEVEN);
        assertThat(longitude().withValue("7", "W").getValue()).isEqualTo(SEVEN.negate());
    }
    
    @Test
    public void shouldReturnSymbolForValueZero() throws Exception {
        assertThat(latitude().withValue(BigDecimal.ZERO).getSymbol()).isEqualTo("N");
    }

}
