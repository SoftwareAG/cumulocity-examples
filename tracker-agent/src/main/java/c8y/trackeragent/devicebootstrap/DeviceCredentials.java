package c8y.trackeragent.devicebootstrap;

public class DeviceCredentials extends com.cumulocity.agent.server.context.DeviceCredentials {

	private String imei;
	private DeviceBootstrapStatus status;
	
	public static DeviceCredentials forDevice(String imei, String tenant, DeviceBootstrapStatus status) {
		return new DeviceCredentials(tenant, null, null, imei, status);
	}
	
	public static DeviceCredentials forAgent(String tenant, String username, String password) {
		return new DeviceCredentials(tenant, username, password, null, null);
	}
	
    private DeviceCredentials(String tenant, String username, String password, String imei, DeviceBootstrapStatus status) {
        super(tenant, username, password, null, null);
        this.imei = imei;
        this.status = status;
    }

    public DeviceCredentials duplicate() {
        return new DeviceCredentials(super.getTenant(), super.getUsername(), super.getPassword(), imei, status);
    }

    public String getImei() {
        return imei;
    }
    
    public DeviceBootstrapStatus getStatus() {
		return status;
	}
    
	public void setImei(String imei) {
		this.imei = imei;
	}
	
	public void setStatus(DeviceBootstrapStatus status) {
		this.status = status;
	}

	@Override
    public String toString() {
        return String.format("DeviceCredentials [tenantId=%s, user=%s, password=%s, imei=%s, status=%s]", 
        		super.getTenant(), super.getUsername(), super.getPassword(), imei, status);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((imei == null) ? 0 : imei.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeviceCredentials other = (DeviceCredentials) obj;
		if (imei == null) {
			if (other.imei != null)
				return false;
		} else if (!imei.equals(other.imei))
			return false;
		if (status != other.status)
			return false;
		return true;
	}
	
	

}
