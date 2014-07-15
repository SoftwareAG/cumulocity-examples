package com.cumulocity.tixi.server.services;

import static com.cumulocity.model.operation.OperationStatus.PENDING;
import static com.cumulocity.tixi.server.model.TixiRequestType.LOG_DEFINITION;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.MeasurementRequestOperation;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.repository.DeviceControlRepository;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.devicecontrol.OperationFilter;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;
import com.cumulocity.tixi.server.model.Operations;

@Component
public class DeviceControlService {
	
	private static final Logger logger = LoggerFactory.getLogger(DeviceControlService.class);

	private static final class SubscriberMessageChannelContext implements MessageChannelContext {
		
		private final Subscription<GId> subscription;

		private SubscriberMessageChannelContext(Subscription<GId> subscription) {
			this.subscription = subscription;
		}

		@Override
		public void close() throws IOException {
			if (subscription != null) {
				subscription.unsubscribe();
			}
		}
	}

    private final DeviceControlRepository repository;

    private final DeviceContextService contextService;

    @Autowired
    public DeviceControlService(DeviceControlRepository repository, DeviceContextService contextService) {
        this.repository = repository;
        this.contextService = contextService;
    }

    public void subscirbe(final MessageChannel<OperationRepresentation> messageChannel) {

        final GId deviceId = (GId) contextService.getCredentials().getDeviceId();
        final Subscription<GId> subscription = repository.subscribe(deviceId, new SubscriptionListener<GId, OperationRepresentation>() {
            @Override
            public void onNotification(final Subscription<GId> subscription, OperationRepresentation notification) {
            	logger.debug("Received operation {}.", notification);
                execute(messageChannel, subscription, notification);
            }

            @Override
            public void onError(Subscription<GId> subscription, Throwable ex) {
                //do nothing
            	logger.error("Error occured for operation subscription for deviceId " + subscription.getObject(), ex);
            }
        });

        for (OperationRepresentation operation : repository.findAllByFilter(
        		new OperationFilter().byDevice(GId.asString(deviceId)).byStatus(PENDING))) {
            execute(messageChannel, subscription, operation);
        }
    }

    private void execute(final MessageChannel<OperationRepresentation> messageChannel, final Subscription<GId> subscription,
            OperationRepresentation operation) {
    	MeasurementRequestOperation measurementRequest = operation.get(MeasurementRequestOperation.class);
    	if(acceptRequest(measurementRequest)) {
    		logger.debug("Measurement request {} accepted.", operation);
    		final OperationRepresentation executingOperation = Operations.asOperation(operation.getId());
    		executingOperation.setStatus(OperationStatus.EXECUTING.name());
    		repository.save(executingOperation);
    		messageChannel.send(new SubscriberMessageChannelContext(subscription), measurementRequest);
    	} else {
    		logger.debug("Operation {} not supported by tixi agent.", operation);
    	}
    }

    
    private static boolean acceptRequest(MeasurementRequestOperation measurementRequest) {
    	return measurementRequest != null && LOG_DEFINITION.name().equals(measurementRequest.getRequestName());
    }
}
