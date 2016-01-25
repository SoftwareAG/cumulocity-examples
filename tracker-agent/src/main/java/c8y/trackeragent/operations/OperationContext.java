package c8y.trackeragent.operations;

import java.util.HashMap;
import java.util.Map;

import c8y.trackeragent.ConnectionContext;

import com.cumulocity.rest.representation.operation.OperationRepresentation;

public class OperationContext extends ConnectionContext {
    
    private final OperationRepresentation operation;
    
    public OperationContext(OperationRepresentation operation, String imei, Map<String, Object> connectionParams) {
        super(imei, connectionParams);
		this.operation = operation;
    }
    
    public OperationContext(OperationRepresentation operation, String imei) {
		this(operation, imei, new HashMap<String, Object>());
    }

    public OperationRepresentation getOperation() {
        return operation;
    }

	@Override
	public String toString() {
		return "OperationContext [operation=" + operation + ", " + super.toString() + "]";
	}

    

}
