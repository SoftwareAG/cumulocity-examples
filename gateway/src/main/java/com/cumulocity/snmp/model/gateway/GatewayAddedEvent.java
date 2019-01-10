package com.cumulocity.snmp.model.gateway;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.snmp.model.core.HasGateway;
import lombok.Data;

@Data
public final class GatewayAddedEvent implements HasGateway {
    private final Gateway gateway;
}
