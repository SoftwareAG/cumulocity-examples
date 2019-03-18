package com.cumulocity.snmp.model.gateway;

import com.cumulocity.snmp.model.core.ConfigEvent;
import com.cumulocity.snmp.model.core.ConfigEventType;
import com.cumulocity.snmp.model.core.GatewayProvider;
import com.cumulocity.snmp.model.gateway.device.Device;
import lombok.Data;

@Data
public class DeviceConfigSuccessEvent implements GatewayProvider, ConfigEvent {
    private final Gateway gateway;
    private final Device device;
    private final ConfigEventType type;

    @Override
    public String getMessage() {
        return ConfigEventType.getMessage(type);
    }
}
