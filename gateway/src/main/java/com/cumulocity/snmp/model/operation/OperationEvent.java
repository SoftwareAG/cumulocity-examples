package com.cumulocity.snmp.model.operation;

import com.cumulocity.snmp.model.core.GatewayProvider;
import com.cumulocity.snmp.model.gateway.Gateway;
import lombok.Data;

@Data
public class OperationEvent implements GatewayProvider {
    private final Gateway gateway;
    private final Operation operation;
}
