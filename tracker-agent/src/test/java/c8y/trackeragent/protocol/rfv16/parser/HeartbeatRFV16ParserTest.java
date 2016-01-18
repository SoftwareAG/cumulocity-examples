package c8y.trackeragent.protocol.rfv16.parser;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.rfv16.RFV16ParserTestSupport;
import c8y.trackeragent.utils.message.TrackerMessage;

public class HeartbeatRFV16ParserTest extends RFV16ParserTestSupport {
    
    private static final String IMEI = "1234567890";
    private HeartbeatRFV16Parser parser;
    private ArgumentCaptor<RFV16AlarmType> alarmTypeCaptor = ArgumentCaptor.forClass(RFV16AlarmType.class);

    @Before
    public void init() {
        parser = new HeartbeatRFV16Parser(trackerAgent, serverMessages, alarmService);
    }
    
    @Test
    public void shouldParseImei() throws Exception {
        TrackerMessage msg = deviceMessages.heartbeat("DB", IMEI, "FFFDFFFF");
        
        String actualIemi = parser.parse(msg.asArray());
        
        assertThat(actualIemi).isEqualTo(IMEI);
    }
    
    @Test
    public void shouldCreateAlarm() throws Exception {
        TrackerMessage msg = deviceMessages.heartbeat("DB", IMEI, "FFFDFFFF");
        ReportContext reportCtx = new ReportContext(msg.asArray(), IMEI, out);
        
        parser.onParsed(reportCtx);
        
        verify(alarmService).createRFV16Alarm(any(ReportContext.class), alarmTypeCaptor.capture(), any(TrackerDevice.class));
        assertThat(alarmTypeCaptor.getAllValues()).containsOnly(RFV16AlarmType.LOW_BATTERY);
    }
    
    @Test
    public void shouldSendPing() throws Exception {
        TrackerMessage msg = deviceMessages.heartbeat("DB", IMEI, "FFFFFFFF");
        ReportContext reportCtx = new ReportContext(msg.asArray(), IMEI, out);
        
        parser.onParsed(reportCtx);
        
        verify(deviceMock).ping();
    }
    

}
