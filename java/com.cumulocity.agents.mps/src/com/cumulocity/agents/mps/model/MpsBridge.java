package com.cumulocity.agents.mps.model;

import org.svenson.JSONProperty;

public class MpsBridge {

    private String deviceUrl;

    @JSONProperty(value = "deviceUrl", ignoreIfNull = true)
    public String getDeviceUrl() {
        return deviceUrl;
    }

    public void setDeviceUrl(String deviceUrl) {
        this.deviceUrl = deviceUrl;
    }
}
