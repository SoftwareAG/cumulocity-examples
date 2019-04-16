package com.cumulocity.snmp.model.gateway;

import com.cumulocity.snmp.model.core.ConfigEvent;
import com.cumulocity.snmp.model.core.ConfigEventType;
import com.cumulocity.snmp.model.core.GatewayProvider;
import lombok.Data;

@Data
public class UnknownTrapOrDeviceEvent implements GatewayProvider, ConfigEvent {

    private final Gateway gateway;
    private final ConfigEventType type;
    private final String fragmentType;

    @Override
    public String getMessage() {
        return ConfigEventType.getMessage(type);
    }
}
