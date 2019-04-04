package com.cumulocity.snmp.model.notification.platform;

import com.cumulocity.rest.representation.operation.OperationRepresentation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class OperationListener {
    public abstract void onCreate(OperationRepresentation value);

    public void onError(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
    }
}
