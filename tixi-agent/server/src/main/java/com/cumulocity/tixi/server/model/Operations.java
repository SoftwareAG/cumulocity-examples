package com.cumulocity.tixi.server.model;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;

public class Operations {

    public static OperationRepresentation asOperation(GId id) {
        final OperationRepresentation operation = new OperationRepresentation();
        operation.setId(id);
        return operation;
    }
}
