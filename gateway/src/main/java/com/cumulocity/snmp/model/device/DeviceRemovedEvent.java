package com.cumulocity.snmp.model.device;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.snmp.model.core.GatewayProvider;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.device.Device;
import lombok.Value;

@Value
public class DeviceRemovedEvent implements GatewayProvider {
    public static final String c8y_OPCUADeviceRemoved = "c8y_SNMPDeviceRemoved";
    private final Gateway gateway;
    private final Device device;

    public GId getDeviceId() {
        return getDevice().getId();
    }
}
