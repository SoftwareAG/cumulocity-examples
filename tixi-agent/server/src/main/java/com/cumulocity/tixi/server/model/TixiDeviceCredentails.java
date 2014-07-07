package com.cumulocity.tixi.server.model;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;

public class TixiDeviceCredentails {
    
    
    private String user;

    private String password;

    private String deviceID;

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

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public static TixiDeviceCredentails from(DeviceCredentialsRepresentation credentials) {
        TixiDeviceCredentails tixiCredentails = new TixiDeviceCredentails();
        tixiCredentails.setUser(credentials.getTenantId() + "/" + credentials.getUsername());
        tixiCredentails.setPassword(credentials.getPassword());
        return tixiCredentails;
    }
}
