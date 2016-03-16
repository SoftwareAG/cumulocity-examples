package c8y.trackeragent.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.repository.InventoryRepository;

import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.configuration.TrackerConfiguration;

@Component
public class TrackerDeviceFactory {
	
	private final TrackerConfiguration configuration;
	private final InventoryRepository inventoryRepository;

	@Autowired
	public TrackerDeviceFactory(TrackerConfiguration configuration, InventoryRepository inventoryRepository) {
		this.configuration = configuration;
		this.inventoryRepository = inventoryRepository;
	}

	public TrackerDevice create(TrackerPlatform platform, String imei) {
		 TrackerDevice result = new TrackerDevice(platform, configuration, imei, inventoryRepository);
		 result.init();
		 return result;
	}

}
