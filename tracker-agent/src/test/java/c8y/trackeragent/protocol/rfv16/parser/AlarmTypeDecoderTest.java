package c8y.trackeragent.protocol.rfv16.parser;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collection;

import org.junit.Test;

public class AlarmTypeDecoderTest {
    
    @Test
    public void shouldParseAlarmStatus() throws Exception {
        doShouldParseAlarmStatus("FFFFFFFF");
        doShouldParseAlarmStatus("00000000", RFV16AlarmType.values());
        doShouldParseAlarmStatus("FFFFFFFD", RFV16AlarmType.SOS);
        doShouldParseAlarmStatus("FFFFFFFB", RFV16AlarmType.OVERSPEED);
        doShouldParseAlarmStatus("FFFDFFFF", RFV16AlarmType.LOW_BATTERY);
    }
    
    private void doShouldParseAlarmStatus(String status, RFV16AlarmType... expectedAlarmTypes) {
        Collection<RFV16AlarmType> actual = AlarmTypeDecoder.getAlarmTypes(status);
        assertThat(actual).containsOnly((Object[]) expectedAlarmTypes);
    }

}
