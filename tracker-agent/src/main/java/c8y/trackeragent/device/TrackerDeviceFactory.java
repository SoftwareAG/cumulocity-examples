package c8y.trackeragent.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;

import c8y.trackeragent.UpdateIntervalProvider;
import c8y.trackeragent.configuration.TrackerConfiguration;

@Component
public class TrackerDeviceFactory {
	
	private final TrackerConfiguration configuration;
	private final InventoryRepository inventoryRepository;
    private final EventApi events;
    private final AlarmApi alarms;
    private final MeasurementApi measurements;
    private final DeviceControlApi deviceControl;
    private final IdentityApi registry;
    private final InventoryApi inventory;
    private final UpdateIntervalProvider updateIntervalProvider;

    @Autowired
	public TrackerDeviceFactory(TrackerConfiguration configuration, InventoryRepository inventoryRepository,
			EventApi events, AlarmApi alarms, MeasurementApi measurements, DeviceControlApi deviceControl,
			IdentityApi registry, InventoryApi inventory, UpdateIntervalProvider updateIntervalProvider) {
		this.configuration = configuration;
		this.inventoryRepository = inventoryRepository;
		this.events = events;
		this.alarms = alarms;
		this.measurements = measurements;
		this.deviceControl = deviceControl;
		this.registry = registry;
		this.inventory = inventory;
		this.updateIntervalProvider = updateIntervalProvider;
	}

	/**
	 * TODO instead of device.init() - execute mo preparation here.
	 */
	public TrackerDevice newTrackerDevice(String tenant, String imei) {
		TrackerDevice device = new TrackerDevice(tenant, imei, configuration, inventoryRepository, events, alarms, measurements, deviceControl, registry, inventory, updateIntervalProvider);
		device.init();
		return device;
	}
    
    

}
