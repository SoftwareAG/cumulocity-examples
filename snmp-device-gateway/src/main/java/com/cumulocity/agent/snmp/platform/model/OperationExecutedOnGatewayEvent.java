package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OperationExecutedOnGatewayEvent {

    private final GId deviceId;

    private final String deviceName;

    private OperationRepresentation operationRepresentation;
}
