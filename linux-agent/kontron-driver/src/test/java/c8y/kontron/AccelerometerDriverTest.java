package c8y.kontron;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.alarm.AlarmApi;

public class AccelerometerDriverTest {
	private Platform platform = mock(Platform.class);
	private AlarmApi alarms = mock(AlarmApi.class);
	private AccelerometerReader reader = mock(AccelerometerReader.class);
	
	@Before
	public void setup() throws Exception {
		when(platform.getAlarmApi()).thenReturn(alarms);
	}
	
	@Test
	public void alarmsAfterInit() throws Exception {
		AccelerometerDriver driver = new AccelerometerDriver(reader);
		driver.initialize(platform);

		when(reader.poll()).thenReturn(false);
		driver.run();
		verify(alarms, never()).create(any(AlarmRepresentation.class));

		when(reader.poll()).thenReturn(true);
		driver.run();
		verify(alarms).create(any(AlarmRepresentation.class));	
	}

	@Test
	public void alarmsAfterUsage() throws Exception {
		AccelerometerDriver driver = new AccelerometerDriver(reader, 1L);
		driver.initialize(platform);
		
		when(reader.poll()).thenReturn(true);
		driver.run();
		verify(alarms).create(any(AlarmRepresentation.class));	

		// Polling shortly afterwards doesn't trigger new alarm.
		driver.run();
		verify(alarms).create(any(AlarmRepresentation.class));

		// Even when there was no motion in between.
		when(reader.poll()).thenReturn(false);
		driver.run();
		verify(alarms).create(any(AlarmRepresentation.class));

		when(reader.poll()).thenReturn(false);
		driver.run();
		verify(alarms).create(any(AlarmRepresentation.class));
	}
}
