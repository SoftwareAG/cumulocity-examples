package c8y.migration;

public class DeviceMigrationRequest {

	private final String imei;

	public DeviceMigrationRequest(String imei) {
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
