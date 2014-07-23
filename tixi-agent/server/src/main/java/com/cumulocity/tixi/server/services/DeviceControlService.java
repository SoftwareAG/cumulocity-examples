package com.cumulocity.tixi.server.services;

import static com.cumulocity.model.operation.OperationStatus.PENDING;
import static com.cumulocity.tixi.server.model.TixiRequestType.LOG;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.MeasurementRequestOperation;
import c8y.inject.DeviceScope;

import com.cumulocity.agent.server.repository.DeviceControlRepository;
import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.devicecontrol.OperationFilter;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;
import com.cumulocity.tixi.server.model.Operations;
import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.cumulocity.tixi.server.resources.TixiRequest;
import com.cumulocity.tixi.server.services.MessageChannel.MessageChannelListener;
import com.cumulocity.tixi.server.services.handler.LogDefinitionRegister;

@Component
public class DeviceControlService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceControlService.class);

    private final TixiRequestFactory requestFactory;

    private final LogDefinitionRegister logDefinitionRegister;

    private final DeviceMessageChannelService deviceMessageChannelService;

    private final DeviceControlRepository repository;

    @Autowired
    public DeviceControlService(DeviceControlRepository repository, DeviceMessageChannelService deviceMessageChannelService,
            TixiRequestFactory requestFactory, LogDefinitionRegister logDefinitionRegister) {
        this.repository = repository;
        this.deviceMessageChannelService = deviceMessageChannelService;
        this.requestFactory = requestFactory;
        this.logDefinitionRegister = logDefinitionRegister;
    }

    private void subscirbe(GId deviceId, final MessageChannel<MeasurementRequestOperation> messageChannel) {

        logger.info("Try subscribe on operations from device {}.", deviceId);
        final Subscription<GId> subscription = repository.subscribe(deviceId, new SubscriptionListener<GId, OperationRepresentation>() {
            @Override
            public void onNotification(final Subscription<GId> subscription, OperationRepresentation notification) {
                logger.debug("Received operation {}.", notification);
                executeMeasurementReqOperation(messageChannel, subscription, notification);
            }

            @Override
            public void onError(Subscription<GId> subscription, Throwable ex) {
                //do nothing
                logger.error("Error occured for operation subscription for deviceId " + subscription.getObject(), ex);
            }
        });

        for (OperationRepresentation operation : repository.findAllByFilter(new OperationFilter().byDevice(GId.asString(deviceId))
                .byStatus(PENDING))) {
            executeMeasurementReqOperation(messageChannel, subscription, operation);
        }
    }

    public void markAllOperationsSuccess(ID agentId) {
        markAllOperations(agentId, OperationStatus.EXECUTING, OperationStatus.SUCCESSFUL);
    }

    public void markAllOperationsFailed(ID agentId) {
        markAllOperations(agentId, OperationStatus.EXECUTING, OperationStatus.FAILED);
    }

    private void markAllOperations(ID agentId, OperationStatus from, OperationStatus to) {
        OperationFilter operationFilter = new OperationFilter().byAgent(agentId.getValue()).byStatus(from);
        for (OperationRepresentation operation : repository.findAllByFilter(operationFilter)) {
            logger.info("Change operation with id {} status from {} to {}.", operation.getId(), from, to);
            operation = Operations.asOperation(operation.getId());
            operation.setStatus(to.name());
            repository.save(operation);
        }
    }

    private void executeMeasurementReqOperation(final MessageChannel<MeasurementRequestOperation> messageChannel,
            final Subscription<GId> subscription, OperationRepresentation operation) {
        MeasurementRequestOperation measurementRequest = operation.get(MeasurementRequestOperation.class);
        if (acceptMeasurementRequest(measurementRequest)) {
            logger.info("Measurement request with id {} accepted.", operation.getId());
            final OperationRepresentation executingOperation = Operations.asOperation(operation.getId());
            executingOperation.setStatus(OperationStatus.EXECUTING.name());
            repository.save(executingOperation);
            messageChannel.send(new SubscriberMessageChannelContext(subscription), measurementRequest);
        } else {
            logger.info("Operation with id {} not supported by tixi agent.", operation.getId());
        }
    }

    private static boolean acceptMeasurementRequest(MeasurementRequestOperation measurementRequest) {
        return measurementRequest != null && LOG.name().equals(measurementRequest.getRequestName());
    }

    public void startOperationExecutor(GId deviceId) {
        subscirbe(deviceId, new OperationMessageChannel());
    }

    private class OperationMessageChannel implements MessageChannel<MeasurementRequestOperation> {

        public void send(MessageChannelListener<MeasurementRequestOperation> context, MeasurementRequestOperation measurementRequest) {
            logger.info("Received measurement request {}.", measurementRequest);
            LogDefinition logDefinition = logDefinitionRegister.getLogDefinition();
            if (logDefinition == null) {
                logger.info("Log definition not availablel skip measurement request.");
                return;
            }
            if (logDefinition.getRecordIds().isEmpty()) {
                logger.warn("Log definition %s has no records!", logDefinition);
                return;
            }
            String recordId = logDefinition.getRecordIds().get(0).getId();
            TixiRequest tixiRequest = requestFactory.createLogRequest(recordId);
            deviceMessageChannelService.send(tixiRequest);
        }
    }

    private static final class SubscriberMessageChannelContext implements MessageChannelListener<MeasurementRequestOperation> {

        private final Subscription<GId> subscription;

        private SubscriberMessageChannelContext(Subscription<GId> subscription) {
            this.subscription = subscription;
        }

        @Override
        public void close() {
            if (subscription != null) {
                subscription.unsubscribe();
            }
        }

        @Override
        public void failed(MeasurementRequestOperation message) {
        }
    }

}
