package com.cumulocity.snmp.model.gateway;

import com.cumulocity.snmp.model.core.HasGateway;
import lombok.Data;

@Data
public final class GatewayRemovedEvent implements HasGateway {
    private final Gateway gateway;
}
