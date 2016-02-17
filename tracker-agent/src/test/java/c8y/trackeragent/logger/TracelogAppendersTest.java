package c8y.trackeragent.logger;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import c8y.trackeragent.MockTrackerPlatform;
import c8y.trackeragent.context.TrackerContext;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

public class TracelogAppendersTest {

    private final MockTrackerPlatform platform1 = new MockTrackerPlatform("tenant_1");
    private final MockTrackerPlatform platform2 = new MockTrackerPlatform("tenant_2");
    private final TrackerContext trackerContext = mock(TrackerContext.class);
    private final TracelogAppenders tracelogAppenders = new TracelogAppenders(trackerContext);        
    private final ArgumentCaptor<AlarmRepresentation> alarmCaptor = ArgumentCaptor.forClass(AlarmRepresentation.class);
    
    @Test
    public void shouldSendAlarmOnErrorLogEvent() throws Exception {
        when(trackerContext.getDevicePlatform("imei_1")).thenReturn(platform1);        
        tracelogAppenders.start("imei_1");        
        when(trackerContext.getDevicePlatform("imei_2")).thenReturn(platform2);        
        tracelogAppenders.start("imei_2");               
        
        PlatformLogger.getLogger("imei_1").error("something happened");
        
        verify(platform1.getAlarmApi(), times(1)).create(alarmCaptor.capture());
        assertThat(alarmCaptor.getValue().getText()).contains("something happened");        
        verify(platform2.getAlarmApi(), never()).create(any(AlarmRepresentation.class));
    }    
}
