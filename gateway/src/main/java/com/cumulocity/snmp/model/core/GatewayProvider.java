package com.cumulocity.snmp.model.core;

import com.cumulocity.snmp.model.gateway.Gateway;

public interface GatewayProvider {
    Gateway getGateway();
}
