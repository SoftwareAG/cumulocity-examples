package c8y.trackeragent.devicebootstrap;

public class DeviceCredentials {

    private String tenantId;
    private String user;
    private String password;
    private String imei;

    public DeviceCredentials duplicate() {
        //@formatter:off
        return new DeviceCredentials()
            .setTenantId(tenantId)
            .setUser(user)
            .setPassword(password)
            .setImei(imei);
        //@formatter:on
    }

    public String getTenantId() {
        return tenantId;
    }

    public DeviceCredentials setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getUser() {
        return user;
    }

    public DeviceCredentials setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public DeviceCredentials setPassword(String password) {
        this.password = password;
        return this;
    }

    public DeviceCredentials setImei(String imei) {
        this.imei = imei;
        return this;
    }

    public String getImei() {
        return imei;
    }

    @Override
    public String toString() {
        return String.format("DeviceCredentials [tenantId=%s, user=%s, password=%s, imei=%s]", tenantId, user, password, imei);
    }

}
