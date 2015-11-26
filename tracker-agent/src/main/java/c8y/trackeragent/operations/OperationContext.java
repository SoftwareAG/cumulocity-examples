package c8y.trackeragent.operations;

import com.cumulocity.rest.representation.operation.OperationRepresentation;

public class OperationContext {
    
    private final OperationRepresentation operation;
    private final String imei;
    
    public OperationContext(OperationRepresentation operation, String imei) {
        this.operation = operation;
        this.imei = imei;
    }

    public OperationRepresentation getOperation() {
        return operation;
    }

    public String getImei() {
        return imei;
    }

    @Override
    public String toString() {
        return String.format("OperationContext [operation=%s, imei=%s]", operation, imei);
    }
    
    

}
