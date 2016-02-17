package c8y.trackeragent.protocol.coban.device;

import static c8y.trackeragent.protocol.coban.device.CobanDeviceFactory.formatLocationReportInterval;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class CobanDeviceFactoryTest {
    
    @Test
    public void shouldFormatTimeIntervalInSeconds() throws Exception {
        assertThat(formatLocationReportInterval(5)).isEqualTo("05s");
        assertThat(formatLocationReportInterval(60)).isEqualTo("01m");
        assertThat(formatLocationReportInterval(90)).isEqualTo("01m");
        assertThat(formatLocationReportInterval(180)).isEqualTo("03m");
    }

}
