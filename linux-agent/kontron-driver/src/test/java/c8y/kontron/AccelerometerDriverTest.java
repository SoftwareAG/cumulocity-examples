package c8y.kontron;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentCaptor;

import c8y.lx.driver.PollingDriver;

import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.alarm.AlarmApi;

public class AccelerometerDriverTest {
	private Platform platform = mock(Platform.class);
	private AlarmApi alarms = mock(AlarmApi.class);
	private ManagedObjectRepresentation source = mock(ManagedObjectRepresentation.class);
	private AccelerometerReader reader = mock(AccelerometerReader.class);
	private AccelerometerDriver driver = new AccelerometerDriver(reader);
	
	@Before
	public void setup() throws Exception {
		when(platform.getAlarmApi()).thenReturn(alarms);
		driver.initialize(platform);
		driver.initializeInventory(source);
	}
	
	@Test
	public void correctAlarmSend() throws Exception {
		when(reader.poll()).thenReturn(true);
		driver.run();

		ArgumentCaptor<AlarmRepresentation> argument = ArgumentCaptor.forClass(AlarmRepresentation.class);
		verify(alarms).create(argument.capture());
		AlarmRepresentation alarm = argument.getValue();
		assertEquals(source, alarm.getSource());
		assertEquals(AccelerometerDriver.MOTION_TYPE, alarm.getType());
		assertEquals(CumulocityAlarmStatuses.ACTIVE.toString(), alarm.getStatus());		
	}
	
	@Test
	public void alarmsAfterInit() throws Exception {
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
	
	@Test
	public void configurationHandling() {
		Properties props = new Properties();
		driver.addDefaults(props);
		
		String prop = props.getProperty(AccelerometerDriver.PROP_PREF + PollingDriver.INTERVAL_PROP);
		assertEquals(AccelerometerDriver.INTERVAL_DEFAULT, Long.parseLong(prop));
				
		prop = props.getProperty(AccelerometerDriver.THRESHOLD);
		assertEquals(AccelerometerDriver.THRESHOLD_DEFAULT, Double.parseDouble(prop), 0.1);
		
		double newThr = 1.1;
		props.setProperty(AccelerometerDriver.THRESHOLD, Double.toString(newThr));
		driver.configurationChanged(props);
		
		verify(reader).setThreshold(AdditionalMatchers.eq(newThr, 0.1));
		
		props = new Properties();
		driver.configurationChanged(props);
		verify(reader).setThreshold(AdditionalMatchers.eq(newThr, 0.1));
	}
}
