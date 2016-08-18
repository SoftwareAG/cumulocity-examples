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
