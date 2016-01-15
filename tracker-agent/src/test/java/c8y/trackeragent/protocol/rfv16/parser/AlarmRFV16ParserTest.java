package c8y.trackeragent.protocol.rfv16.parser;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.rfv16.RFV16ParserTestSupport;
import c8y.trackeragent.utils.message.TrackerMessage;

public class AlarmRFV16ParserTest extends RFV16ParserTestSupport {
    
    private static final String IMEI = "1234567890";
    private AlarmRFV16Parser parser;
    private ArgumentCaptor<RFV16AlarmType> alarmTypeCaptor = ArgumentCaptor.forClass(RFV16AlarmType.class);

    @Before
    public void init() {
        parser = new AlarmRFV16Parser(trackerAgent, serverMessages, alarmService);
    }
    
    @Test
    public void shouldParseAlarmStatus() throws Exception {
        doShouldParseAlarmStatus("FFFFFFFF");
        doShouldParseAlarmStatus("00000000", RFV16AlarmType.values());
        doShouldParseAlarmStatus("FFFFFFFD", RFV16AlarmType.SOS);
        doShouldParseAlarmStatus("FFFFFFFB", RFV16AlarmType.OVERSPEED);
        doShouldParseAlarmStatus("FFFDFFFF", RFV16AlarmType.LOW_BATTERY);
    }
    
    private void doShouldParseAlarmStatus(String status, RFV16AlarmType... expectedAlarmTypes) {
        Collection<RFV16AlarmType> actual = parser.getAlarmTypes(status);
        assertThat(actual).containsOnly((Object[]) expectedAlarmTypes);
    }
    
    @Test
    public void shouldParseImei() throws Exception {
        TrackerMessage msg = deviceMessages.status("DB", IMEI, "FFFDFFFF");
        
        String actualIemi = parser.parse(msg.asArray());
        
        assertThat(actualIemi).isEqualTo(IMEI);
    }
    
    @Test
    public void shouldCreateAlarm() throws Exception {
        TrackerMessage msg = deviceMessages.status("DB", IMEI, "FFFDFFFF");
        ReportContext reportCtx = new ReportContext(msg.asArray(), IMEI, out);
        
        parser.onParsed(reportCtx);
        
        verify(alarmService).createRFV16Alarm(any(ReportContext.class), alarmTypeCaptor.capture(), any(TrackerDevice.class));
        assertThat(alarmTypeCaptor.getAllValues()).containsOnly(RFV16AlarmType.LOW_BATTERY);
    }
    

}
