package com.cumulocity.agent.server.context;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Supplier;

public class DeviceBootstrapDeviceCredentialsSupplier implements Supplier<DeviceCredentials> {

    private final String appKey;

    @Inject
    public DeviceBootstrapDeviceCredentialsSupplier(@Value("${C8Y.appKey}") String appKey) {
        this.appKey = appKey;
    }

    @Override
    public DeviceCredentials get() {
        return new DeviceCredentials("management", "devicebootstrap", "", appKey, "");
    }

}
