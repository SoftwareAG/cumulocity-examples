package c8y.migration;

import java.util.ArrayList;
import java.util.List;

public class TenantMigrationRequest {

	private final String tenant;
	private final List<DeviceMigrationRequest> devices = new ArrayList<>();

	public TenantMigrationRequest(String tenant) {
		this.tenant = tenant;
	}

	public String getTenant() {
		return tenant;
	}

	@Override
	public String toString() {
		return "[tenant=" + tenant + ", devices=" + devices + "]";
	}

	public void add(DeviceMigrationRequest deviceMigrationRequest) {
		devices.add(deviceMigrationRequest);
	}
	
	public List<DeviceMigrationRequest> getDevices() {
		return devices;
	}


}
