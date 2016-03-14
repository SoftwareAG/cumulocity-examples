package c8y.trackeragent.devicebootstrap;

public class DeviceCredentials extends com.cumulocity.agent.server.context.DeviceCredentials {

	private String imei;
	private DeviceBootstrapStatus status;
	
	public static DeviceCredentials forDevice(String imei, String tenant) {
		return new DeviceCredentials(tenant, null, null, imei);
	}
	
	public static DeviceCredentials forAgent(String tenant, String username, String password) {
		return new DeviceCredentials(tenant, username, password, null);
	}
	
    private DeviceCredentials(String tenant, String username, String password, String imei) {
        super(tenant, username, password, null, null);
        this.imei = imei;
    }

    public DeviceCredentials duplicate() {
        return new DeviceCredentials(super.getTenant(), super.getUsername(), super.getPassword(), imei);
    }

    public String getImei() {
        return imei;
    }
    
    public DeviceBootstrapStatus getStatus() {
		return status;
	}

	@Override
    public String toString() {
        return String.format("DeviceCredentials [tenantId=%s, user=%s, password=%s, imei=%s, status=%s]", 
        		super.getTenant(), super.getUsername(), super.getPassword(), imei, status);
    }

}
