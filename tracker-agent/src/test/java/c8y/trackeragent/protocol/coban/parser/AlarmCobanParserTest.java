package c8y.trackeragent.protocol.coban.parser;

import static com.cumulocity.model.event.CumulocitySeverities.MAJOR;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.utils.message.TrackerMessage;

import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

public class AlarmCobanParserTest extends CobanParserTestSupport {

    private static final String IMEI = "123123";
    
    private AlarmCobanParser alarmCobanParser;
    private ArgumentCaptor<AlarmRepresentation> alarmCaptor;

    @Before
    public void init() {
        alarmCobanParser = new AlarmCobanParser(trackerAgent);
        alarmCaptor = ArgumentCaptor.forClass(AlarmRepresentation.class);
    }

    @Test
    public void shouldAcceptAlarms() throws Exception {
        for (AlarmType alarmType : AlarmType.values()) {
            TrackerMessage msg = deviceMessages.alarm(IMEI, alarmType);

            assertThat(alarmCobanParser.accept(msg.asArray())).isTrue();
        }
    }
    
    @Test
    public void shouldCreateLowBatteryAlarm() throws Exception {
        alarmCobanParser.onParsed(anAlarmReport(AlarmType.LOW_BATTERY));
        
        verify(deviceMock).createAlarm(alarmCaptor.capture());
        AlarmRepresentation actual = alarmCaptor.getValue();
        checkCommonAlarmProperties(actual);
        assertThat(actual.getType()).isEqualTo("c8y_LowBattery");
        assertThat(actual.getSeverity()).isEqualTo(MAJOR.toString());
    }
    
    @Test
    public void shouldCreateAlarms() throws Exception {
        for (AlarmType alarmType : AlarmType.values()) {
            Mockito.reset(deviceMock);
            
            alarmCobanParser.onParsed(anAlarmReport(alarmType));
            
            verify(deviceMock).createAlarm(alarmCaptor.capture());
            AlarmRepresentation actual = alarmCaptor.getValue();
            checkCommonAlarmProperties(actual);
            assertThat(actual.getType()).isEqualTo(alarmType.asC8yType());
            assertThat(actual.getSeverity()).isNotNull();
        }
    }

    private ReportContext anAlarmReport(AlarmType alarmType) {
        String[] report = deviceMessages.alarm(IMEI, alarmType).asArray();
        return new ReportContext(report, IMEI, out);
    }
    
    private void checkCommonAlarmProperties(AlarmRepresentation actual) {
        assertThat(actual.getSource().getId()).isEqualTo(deviceMock.getGId());
        assertThat(actual.getTime()).isNotNull();
        assertThat(actual.getText()).isNotNull();
        assertThat(actual.getStatus()).isEqualTo(CumulocityAlarmStatuses.ACTIVE.toString());
    }

}
