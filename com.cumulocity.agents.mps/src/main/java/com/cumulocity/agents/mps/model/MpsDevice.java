/*
 * Copyright 2012 Nokia Siemens Networks 
 */
package com.cumulocity.agents.mps.model;

import org.svenson.JSONProperty;

import com.cumulocity.model.control.Relay.RelayState;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.sdk.agent.model.Device;

public class MpsDevice implements Device {

	public static final String  MEASUREMENTS_URL_PATTERN = "%s/meter/readout/%s/en";
	
	public static final String CHANGE_STATE_URL_PATTERN = "%s/meter/relay/%s/%s";
	
	private GId globalId;
	
    private String deviceUrl;

    private String deviceId;
    
    @Override
    @JSONProperty(ignore = true)
    public GId getGlobalId() {
    	return globalId;
    }
    
    public void setGlobalId(GId globalId) {
		this.globalId = globalId;
	}

    @JSONProperty(value = "deviceUrl", ignoreIfNull = true)
    public String getDeviceUrl() {
        return deviceUrl;
    }

    public void setDeviceUrl(String deviceUrl) {
        this.deviceUrl = deviceUrl;
    }

    @JSONProperty(value = "deviceId", ignoreIfNull = true)
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @JSONProperty(ignore = true)
    public String getMeasurementsUrl() {
    	return String.format(MEASUREMENTS_URL_PATTERN, deviceUrl, deviceId);
    }
    
    @JSONProperty(ignore = true)
    public String getChangeStateUrl(RelayState state) {
    	return String.format(CHANGE_STATE_URL_PATTERN, deviceUrl, deviceId, RelayState.OPEN.equals(state) ? "ON" : "OFF");
    }
}
