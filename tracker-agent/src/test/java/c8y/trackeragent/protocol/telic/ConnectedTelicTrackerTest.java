package c8y.trackeragent.protocol.telic;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class ConnectedTelicTrackerTest {
    
    @Test
    public void shouldEatBeginingOfString() throws Exception {
        assertThat(ConnectedTelicTracker.eat("abc", 1)).isEqualTo("bc");
        assertThat(ConnectedTelicTracker.eat("abc", 2)).isEqualTo("c");
        assertThat(ConnectedTelicTracker.eat("abc", 3)).isEmpty();
    }
    
    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfReportTooShort() throws Exception {
        ConnectedTelicTracker.eat("abc", 4);
    }

}
