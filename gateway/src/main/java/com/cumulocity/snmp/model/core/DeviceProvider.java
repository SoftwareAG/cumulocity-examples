package com.cumulocity.snmp.model.core;

import com.cumulocity.snmp.model.gateway.device.Device;

public interface DeviceProvider {
    Device getDevice();
}
