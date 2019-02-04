package com.cumulocity.snmp.model.device;

import com.cumulocity.snmp.model.core.GatewayProvider;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.device.Device;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class DeviceAddedEvent implements GatewayProvider {
    public static final String c8y_SNMPDeviceAdded = "c8y_SNMPDeviceAdded";
    private final Gateway gateway;
    private final Device device;
}
