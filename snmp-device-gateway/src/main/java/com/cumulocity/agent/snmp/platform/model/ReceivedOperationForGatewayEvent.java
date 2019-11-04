package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReceivedOperationForGatewayEvent {

    public static final String C8Y_SNMP_AUTO_DISCOVERY_FRAGMENT_KEY = "c8y_SnmpAutoDiscovery";
    public static final String IP_RANGE_KEY = "ipRange";


    private final GId deviceId;

    private final String deviceName;

    private OperationRepresentation operationRepresentation;
}
