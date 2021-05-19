package c8y.trackeragent.devicebootstrap;

import com.cumulocity.microservice.context.credentials.UserCredentials;
import com.cumulocity.model.idtype.GId;
import com.google.common.base.Predicate;

public class DeviceCredentials extends UserCredentials {

	private GId deviceId;
	private int pageSize;
	private String imei;
	
	public static DeviceCredentials forDevice(String imei, String tenant) {
		return new DeviceCredentials(tenant, null, null, imei, null);
	}
	
	public static DeviceCredentials forAgent(String tenant, String username, String password) {
		return new DeviceCredentials(tenant, username, password, null, null);
	}
	
	public static DeviceCredentials forAgent(String tenant, String username, String password, GId deviceId) {
		return new DeviceCredentials(tenant, username, password, null, deviceId);
	}
	
    public DeviceCredentials(String tenant, String username, String password, String imei, GId deviceId) {
        super(tenant, username, password, null, null, imei, null, null);
        this.deviceId = deviceId;
        this.imei = imei;
    }

    public DeviceCredentials duplicate() {
        return new DeviceCredentials(getTenant(), getUsername(), getPassword(), imei, deviceId);
    }

	@Override
	public String getOAuthAccessToken() {
		return null;
	}

	@Override
	public String getXsrfToken() {
		return null;
	}

	public GId getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(GId deviceId) {
		this.deviceId = deviceId;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getImei() {
        return imei;
    }
    
	public void setImei(String imei) {
		this.imei = imei;
	}
	
	@Override
    public String toString() {
        return String.format("DeviceCredentials [tenantId=%s, user=%s, password=%s, imei=%s]", 
        		getTenant(), getUsername(), getUsername(), imei);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((imei == null) ? 0 : imei.hashCode());
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
		return true;
	}
	
	public static Predicate<DeviceCredentials> hasTenant(final String tenant) {
		return new Predicate<DeviceCredentials>() {

			@Override
			public boolean apply(DeviceCredentials input) {
				return input.getTenant().equals(tenant);
			}
		};
	}

}
