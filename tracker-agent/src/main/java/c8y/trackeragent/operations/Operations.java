package c8y.trackeragent.operations;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;

import static com.cumulocity.model.operation.OperationStatus.*;

public class Operations {

    public static OperationRepresentation asExecutingOperation(GId id) {
        return asOperationWithStatus(id, EXECUTING);
    }

    public static OperationRepresentation asSuccessOperation(GId id) {
        return asOperationWithStatus(id, SUCCESSFUL);
    }

    public static OperationRepresentation asOperationWithStatus(GId id, OperationStatus status) {
        final OperationRepresentation operation = new OperationRepresentation();
        operation.setId(id);
        operation.setStatus(status.name());
        return operation;
    }

    public static OperationRepresentation asFailedOperation(GId id, String failureCause) {
        final OperationRepresentation operation = new OperationRepresentation();
        operation.setId(id);
        operation.setStatus(FAILED.name());
        operation.setFailureReason(failureCause);
        return operation;
    }

}
