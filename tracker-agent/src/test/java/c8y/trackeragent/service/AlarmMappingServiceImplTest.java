package c8y.trackeragent.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.protocol.coban.parser.CobanAlarmType;
import c8y.trackeragent.protocol.rfv16.parser.RFV16AlarmType;

public class AlarmMappingServiceImplTest {
    
    AlarmMappingServiceImpl alarmMapping;
    
    @Before
    public void init() {
        alarmMapping = new AlarmMappingServiceImpl("alarm-configuration");
        alarmMapping.init();
    }
    
    @Test
    public void shouldReadAlarmMappings() throws Exception {
        String actual = alarmMapping.getType("low_battery");
        
        assertThat(actual).isEqualTo("c8y_LowBattery");
        
        actual = alarmMapping.getText("low_battery");
        
        assertThat(actual).isEqualTo("Batteriezustand ist kritisch.");
        
        actual = alarmMapping.getSeverity("low_battery");
        
        assertThat(actual).isEqualTo("MAJOR");
    }
    
    @Test
    public void shouldReadAlarmMappingsForKeyWithSpace() throws Exception {
        String actual = alarmMapping.getType("low battery");
        
        assertThat(actual).isEqualTo("c8y_LowBattery");
    }
    
    @Test
    public void shouldHaveMappingForAllCobanAlarms() throws Exception {
        doShouldHaveMappingForAllAlarmTypes(CobanAlarmType.values());
    }
    
    @Test
    public void shouldHaveMappingForAllRFV16Alarms() throws Exception {
        doShouldHaveMappingForAllAlarmTypes(RFV16AlarmType.values());
    }
    
    private <A extends AlarmType> void doShouldHaveMappingForAllAlarmTypes(A[] alarmTypes) throws Exception {
        for (AlarmType alarmType : alarmTypes) {
            String actual = alarmMapping.getType(alarmType.name());
            String key = AlarmMappingServiceImpl.asCode(alarmType.name(), AlarmMappingServiceImpl.ENTRY_TYPE);
            assertThat(actual).describedAs("missing mapping for " + key).isNotEqualTo(key);

            actual = alarmMapping.getText(alarmType.name());
            key = AlarmMappingServiceImpl.asCode(alarmType.name(), AlarmMappingServiceImpl.ENTRY_TEXT);
            assertThat(actual).describedAs("missing mapping for " + key).isNotEqualTo(key);

            actual = alarmMapping.getSeverity(alarmType.name());
            key = AlarmMappingServiceImpl.asCode(alarmType.name(), AlarmMappingServiceImpl.ENTRY_SEVERITY);
            assertThat(actual).describedAs("missing mapping for " + key).isNotEqualTo(key);
        }
    }

}
