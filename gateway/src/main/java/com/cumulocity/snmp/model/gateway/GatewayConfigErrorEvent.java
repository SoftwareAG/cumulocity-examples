package com.cumulocity.snmp.model.gateway;

import com.cumulocity.snmp.model.core.ConfigEvent;
import com.cumulocity.snmp.model.core.ConfigEventType;
import com.cumulocity.snmp.model.core.GatewayProvider;
import lombok.Data;

@Data
public class GatewayConfigErrorEvent implements GatewayProvider, ConfigEvent {
    private final Gateway gateway;
    private final ConfigEventType type;

    @Override
    public String getMessage() {
        return ConfigEventType.getMessage(type);
    }
}
