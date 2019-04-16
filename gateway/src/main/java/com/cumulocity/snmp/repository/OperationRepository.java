package com.cumulocity.snmp.repository;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.model.gateway.Gateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OperationRepository {

    public static final String SUCCESSFUL = "SUCCESSFUL";
    public static final String FAILED = "FAILED";
    public static final String EXECUTING = "EXECUTING";

    private final DeviceControlApi deviceControlApi;

    @RunWithinContext
    public void successful(Gateway gateway, GId operationId) {
        try {
            final OperationRepresentation operation = new OperationRepresentation();
            operation.setId(operationId);
            operation.setStatus(SUCCESSFUL);
            deviceControlApi.update(operation);
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @RunWithinContext
    public void executing(Gateway gateway, GId operationId) {
        try {
            final OperationRepresentation operation = new OperationRepresentation();
            operation.setId(operationId);
            operation.setStatus(EXECUTING);
            deviceControlApi.update(operation);
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @RunWithinContext
    public void failed(Gateway gateway, GId operationId, String message) {
        try {
            final OperationRepresentation operation = new OperationRepresentation();
            operation.setId(operationId);
            operation.setStatus(FAILED);
            operation.setFailureReason(message);
            deviceControlApi.update(operation);
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
