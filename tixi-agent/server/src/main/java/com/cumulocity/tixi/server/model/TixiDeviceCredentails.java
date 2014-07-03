package com.cumulocity.tixi.server.model;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;

public class TixiDeviceCredentails {
    
    
    private String username;

    private String password;

    private String deviceId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public static TixiDeviceCredentails from(DeviceCredentialsRepresentation credentials) {
        TixiDeviceCredentails tixiCredentails = new TixiDeviceCredentails();
        tixiCredentails.setUsername(credentials.getTenantId() + "/" + credentials.getUsername());
        tixiCredentails.setPassword(credentials.getPassword());
        return tixiCredentails;
    }
}
