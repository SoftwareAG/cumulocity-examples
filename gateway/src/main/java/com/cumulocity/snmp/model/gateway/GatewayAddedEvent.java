package com.cumulocity.snmp.model.gateway;

import com.cumulocity.snmp.model.core.GatewayProvider;
import lombok.Data;

@Data
public final class GatewayAddedEvent implements GatewayProvider {
    private final Gateway gateway;
}
