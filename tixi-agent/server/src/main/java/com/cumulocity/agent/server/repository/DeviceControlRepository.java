package com.cumulocity.agent.server.repository;

import javax.inject.Named;

import jersey.repackaged.com.google.common.collect.Iterables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.devicecontrol.OperationFilter;
import com.cumulocity.sdk.client.devicecontrol.PagedOperationCollectionRepresentation;
import com.cumulocity.sdk.client.notification.Subscriber;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;
import com.cumulocity.tixi.server.model.Operations;

@Component
public class DeviceControlRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(DeviceControlRepository.class);

    private final DeviceControlApi deviceControlApi;

    private Subscriber<GId, OperationRepresentation> subscriber;

    @Autowired
    public DeviceControlRepository(DeviceControlApi deviceControlApi,
            @Named("deviceControlNotificationsSubscriber") Subscriber<GId, OperationRepresentation> subscriber) {
        this.deviceControlApi = deviceControlApi;
        this.subscriber = subscriber;
    }

    public OperationRepresentation findOneByFilter(OperationFilter filter) {
        return Iterables.get(loadByFilter(filter).elements(1), 0);
    }

    public Iterable<OperationRepresentation> findAllByFilter(OperationFilter filter) {
        return loadByFilter(filter).allPages();
    }
    
    public void markAllOperationsSuccess(ID agentId) {
    	markAllOperations(agentId, OperationStatus.EXECUTING, OperationStatus.SUCCESSFUL);
    }
    
    public void markAllOperationsFailed(ID agentId) {
    	markAllOperations(agentId, OperationStatus.EXECUTING, OperationStatus.FAILED);
    }
    
	private void markAllOperations(ID agentId, OperationStatus from, OperationStatus to) {
		OperationFilter operationFilter = new OperationFilter().byAgent(agentId.getValue()).byStatus(from);
		for (OperationRepresentation operation : findAllByFilter(operationFilter)) {
			logger.info("Change operation with id {} status from {} to {}.", operation.getId(), from, to);
			operation = Operations.asOperation(operation.getId());
			operation.setStatus(to.name());
			deviceControlApi.update(operation);
		}
	}

    private PagedOperationCollectionRepresentation loadByFilter(OperationFilter filter) {
        return deviceControlApi.getOperationsByFilter(filter).get();
    }

    public Subscription<GId> subscribe(GId deviceId, SubscriptionListener<GId, OperationRepresentation> listener) {
        return subscriber.subscribe(deviceId, listener);
    }

    public void save(OperationRepresentation operation) {
        if (operation.getId() == null) {
            deviceControlApi.create(operation);
        } else {
            deviceControlApi.update(operation);
        }
    }
}
