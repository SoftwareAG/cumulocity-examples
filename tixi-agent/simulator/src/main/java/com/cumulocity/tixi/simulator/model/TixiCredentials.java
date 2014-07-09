package com.cumulocity.tixi.simulator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TixiCredentials {
    
    public static final String DEVICE_SERIAL = "669";

    public String user;
    public String password;
    public String deviceID;
}
