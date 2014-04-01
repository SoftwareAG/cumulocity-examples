package c8y.trackeragent.logger;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import c8y.trackeragent.MockTrackerPlatform;

import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

public class TracelogDriverTest {

    @Test
    public void shouldSendEventOnInfoLogEvent() throws Exception {
        ArgumentCaptor<EventRepresentation> eventCaptor = ArgumentCaptor.forClass(EventRepresentation.class);
        MockTrackerPlatform platform1 = new MockTrackerPlatform("tenant_1");
        MockTrackerPlatform platform2 = new MockTrackerPlatform("tenant_2");
        new TracelogDriver(platform1, new ManagedObjectRepresentation());
        new TracelogDriver(platform2, new ManagedObjectRepresentation());
        
        PlatformLogger.getLogger(platform1.getTenantId()).warn("somethink happened");
        
        verify(platform1.getEventApi(), times(1)).create(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getText()).isEqualTo("somethink happened");
        
        verify(platform2.getEventApi(), never()).create(any(EventRepresentation.class));
    }
}
