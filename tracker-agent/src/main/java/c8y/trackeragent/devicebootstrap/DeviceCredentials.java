package c8y.trackeragent.devicebootstrap;

public class DeviceCredentials {

    private String tenantId;
    private String user;
    private String password;
    private String imei;
    
    public DeviceCredentials() {}

    public DeviceCredentials(String tenantId, String user, String password, String imei) {
        this.tenantId = tenantId;
        this.user = user;
        this.password = password;
        this.imei = imei;
    }
    
    public DeviceCredentials duplicate() {
        return new DeviceCredentials(tenantId, user, password, imei);
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setImei(String imei) {
        this.imei = imei;
    }
    
    public String getImei() {
        return imei;
    }

    @Override
    public String toString() {
        return String.format("DeviceCredentials [tenantId=%s, user=%s, password=%s, imei=%s]", tenantId, user, password, imei);
    }

}
