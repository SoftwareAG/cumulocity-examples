package c8y.trackeragent.devicebootstrap;

import com.cumulocity.model.idtype.GId;

public class DeviceCredentials extends com.cumulocity.agent.server.context.DeviceCredentials {

    public DeviceCredentials(String tenant, String username, String password, String appKey, GId deviceId) {
        super(tenant, username, password, appKey, deviceId);
    }

    private String imei;

    public DeviceCredentials duplicate() {
        //@formatter:off
        return new DeviceCredentials(super.getTenant(), super.getUsername(), super.getPassword(), null, null)
            .setImei(imei);
        //@formatter:on
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
        return String.format("DeviceCredentials [tenantId=%s, user=%s, password=%s, imei=%s]", super.getTenant(), super.getUsername(), super.getPassword(), imei);
    }

}
