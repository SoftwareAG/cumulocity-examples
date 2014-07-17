package com.cumulocity.tixi.server.model;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;

public class TixiDeviceCredentails {
    
    public static final String USERNAME_SEPARATOR = "*";

	private String user;

    private String password;

    private String deviceID;

    public TixiDeviceCredentails() {
    }
    
    public TixiDeviceCredentails(String user, String password, String deviceID) {
        this.user = user;
        this.password = password;
        this.deviceID = deviceID;
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

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public static TixiDeviceCredentails from(DeviceCredentialsRepresentation credentials) {
        TixiDeviceCredentails tixiCredentails = new TixiDeviceCredentails();
        tixiCredentails.setUser(credentials.getTenantId() + USERNAME_SEPARATOR + credentials.getUsername());
        tixiCredentails.setPassword(credentials.getPassword());
        tixiCredentails.setDeviceID(credentials.getId());
        return tixiCredentails;
    }

	@Override
    public String toString() {
	    return String.format("TixiDeviceCredentails [user=%s, password=%s, deviceID=%s]", user, password, deviceID);
    }
}
