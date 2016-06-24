package c8y.migration.model;

public class DeviceMigrationRequest {

	private final String imei;
	private final String user;
	private final String password;

	public DeviceMigrationRequest(String imei, String user, String password) {
		this.imei = imei;
		this.user = user;
		this.password = password;
	}

	public String getImei() {
		return imei;
	}
	
	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "DeviceMigrationRequest [imei=" + imei + ", user=" + user + ", password=" + password + "]";
	}
}
