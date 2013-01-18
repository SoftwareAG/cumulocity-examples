/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
