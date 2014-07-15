package com.cumulocity.tixi.simulator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TixiCredentials {
    
    public String user;
    public String password;
    public String deviceID;
    
	@Override
    public String toString() {
	    return String.format("TixiCredentials [user=%s, password=%s, deviceID=%s]", user, password, deviceID);
    }
}
    
