/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.context;

import com.cumulocity.rest.representation.operation.OperationRepresentation;

import c8y.trackeragent.server.ConnectionDetails;

public class OperationContext extends ConnectionContext {
    
    private final OperationRepresentation operation;
    
    public OperationContext(ConnectionDetails connectionDetails, OperationRepresentation operation) {
        super(connectionDetails);
		this.operation = operation;
    }
    
    public OperationRepresentation getOperation() {
        return operation;
    }

	@Override
	public String toString() {
		return "OperationContext [operation=" + operation + ", " + super.toString() + "]";
	}

    

}
