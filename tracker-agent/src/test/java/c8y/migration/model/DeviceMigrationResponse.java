package c8y.migration.model;

public class DeviceMigrationResponse {

	private final String imei;

	public DeviceMigrationResponse(String imei) {
		this.imei = imei;
	}

	public String getImei() {
		return imei;
	}

	@Override
	public String toString() {
		return imei;
	}
	
}
