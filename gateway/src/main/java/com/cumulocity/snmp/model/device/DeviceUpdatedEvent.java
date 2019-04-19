package com.cumulocity.snmp.model.device;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.snmp.model.core.GatewayProvider;
import com.cumulocity.snmp.model.gateway.Gateway;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class DeviceUpdatedEvent implements GatewayProvider {
    private final Gateway gateway;
    private final GId deviceId;
}
