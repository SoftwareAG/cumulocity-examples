package c8y.trackeragent.protocol.rfv16.parser;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.protocol.rfv16.RFV16ParserTestSupport;

public class AlarmRFV16ParserTest extends RFV16ParserTestSupport {
    
    private AlarmRFV16Parser parser;

    @Before
    public void init() {
        parser = new AlarmRFV16Parser(trackerAgent, serverMessages);
    }
    
    @Test
    public void shouldParseAlarmStatus() throws Exception {
        doShouldParseAlarmStatus("FFFFFFFF");
        doShouldParseAlarmStatus("00000000", AlarmType.values());
        doShouldParseAlarmStatus("FEFFFFFF", AlarmType.NOISE_SENSOR);
        doShouldParseAlarmStatus("FFFFFEFF", AlarmType.DOOR);
        doShouldParseAlarmStatus("FFFFFFFE", AlarmType.THEFT);
        doShouldParseAlarmStatus("FFFDFFFF", AlarmType.LOW_BATTERY);
    }
    
    private void doShouldParseAlarmStatus(String status, AlarmType... expectedAlarmTypes) {
        Collection<AlarmType> actual = parser.getAlarmTypes(status);
        assertThat(actual).containsOnly((Object[]) expectedAlarmTypes);
    }

}
