package com.cumulocity.snmp.model.gateway;

import com.cumulocity.snmp.model.core.GatewayProvider;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.type.DeviceType;
import lombok.Data;

@Data
public class DeviceTypeUpdatedEvent implements GatewayProvider {
    private final Gateway gateway;
    private final Device device;
    private final DeviceType deviceType;
}

