package c8y.trackeragent.utils;

import static c8y.trackeragent.utils.LocationEventBuilder.aLocationEvent;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;

import c8y.SpeedMeasurement;

public class LocationEventBuilderTest {
    
    List<AlarmRepresentation> alarms = asList(anAlarm("alarm1"), anAlarm("alarm2"));
    SpeedMeasurement speedMeasurement;
    
    @Before
    public void init() {
        MeasurementValue speed = new MeasurementValue(BigDecimal.valueOf(60.0), "km/h", null, null, null);
        speedMeasurement = new SpeedMeasurement();
        speedMeasurement.setSpeed(speed);
    }
    
    @Test
    public void shouldCreateLocationEvent() throws Exception {
        EventRepresentation event = aLocationEvent().withAlarms(alarms).build();
        
        assertThat(event.getSource()).isNotNull();
        assertThat(event.getText()).isNotNull();
        assertThat(event.getType()).isNotNull();
        assertThat(event.getTime()).isNotNull();
    }
    
    @Test
    public void shouldCreateLocationEventWithAlarms() throws Exception {
        EventRepresentation event = aLocationEvent().withAlarms(alarms).build();
        
        assertThat(event.getText()).isEqualTo("alarm1|alarm2");
    }
    
    @Test
    public void shouldCreateLocationEventWithSpeed() throws Exception {
        EventRepresentation event = aLocationEvent().withSpeedMeasurement(speedMeasurement).build();
        
        assertThat(event.getText()).isEqualTo("60.0 km/h");
    }
    
    @Test
    public void shouldCreateLocationEventWithAlarmsAndSpeed() throws Exception {
        EventRepresentation event = aLocationEvent().withAlarms(alarms).withSpeedMeasurement(speedMeasurement).build();
        
        assertThat(event.getText()).isEqualTo("60.0 km/h alarm1|alarm2");
    }

    private AlarmRepresentation anAlarm(String alarm1) {
        AlarmRepresentation alarm = new AlarmRepresentation();
        alarm.setText(alarm1);
        return alarm;
    }

}
