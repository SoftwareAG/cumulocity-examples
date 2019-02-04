package com.cumulocity.snmp.model.gateway;

import com.cumulocity.snmp.model.core.GatewayProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public final class GatewayUpdateEvent implements GatewayProvider {
    private final Gateway gateway;
}
