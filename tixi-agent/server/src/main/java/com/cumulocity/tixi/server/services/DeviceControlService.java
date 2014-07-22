package com.cumulocity.tixi.server.services;

import static com.cumulocity.model.operation.OperationStatus.PENDING;
import static com.cumulocity.tixi.server.model.TixiRequestType.LOG;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.MeasurementRequestOperation;

import com.cumulocity.agent.server.context.DeviceContext;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.context.DeviceCredentials;
import com.cumulocity.agent.server.repository.DeviceControlRepository;
import com.cumulocity.agent.server.repository.IdentityRepository;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.cumulocity.sdk.client.devicecontrol.OperationFilter;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;
import com.cumulocity.sdk.client.polling.PollingStrategy;
import com.cumulocity.tixi.server.model.Operations;
import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.TixiDeviceCredentails;

@Component
public class DeviceControlService {
	
	private static final long BOOTSTRAP_TIMEOUT_IN_SECONDS = 300L;
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
    private final IdentityRepository identityRepository;
    private final DeviceCredentialsApi deviceCredentials;
    private final DeviceContextService contextService;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public DeviceControlService(DeviceControlRepository repository, IdentityRepository identityRepository, DeviceCredentialsApi deviceCredentials,
            DeviceContextService contextService, InventoryRepository inventoryRepository) {
	    this.repository = repository;
	    this.identityRepository = identityRepository;
	    this.deviceCredentials = deviceCredentials;
	    this.contextService = contextService;
		this.inventoryRepository = inventoryRepository;
    }

	public void subscirbe(final MessageChannel<OperationRepresentation> messageChannel) {

        final GId deviceId = (GId) contextService.getCredentials().getDeviceId();
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

        for (OperationRepresentation operation : repository.findAllByFilter(
        		new OperationFilter().byDevice(GId.asString(deviceId)).byStatus(PENDING))) {
            executeMeasurementReqOperation(messageChannel, subscription, operation);
        }
    }
	
    public TixiDeviceCredentails register(final SerialNumber serialNumber) {

        final DeviceCredentialsRepresentation credentials = deviceCredentials.pollCredentials(serialNumber.getValue(), 
        		new PollingStrategy(BOOTSTRAP_TIMEOUT_IN_SECONDS, SECONDS, asList(10L)));
        TixiDeviceCredentails tixiCredentials = TixiDeviceCredentails.from(credentials);

        try {
            ManagedObjectRepresentation deviceRepresentation = contextService.callWithinContext(
                    new DeviceContext(DeviceCredentials.from(credentials)), new Callable<ManagedObjectRepresentation>() {
                        @Override
                        public ManagedObjectRepresentation call() throws Exception {
                            return inventoryRepository.saveAgentIfNotExists("c8y_TixiAgent", "c8y_TixiAgent_" + serialNumber.getValue(), serialNumber, null);
                        }
                    });
            tixiCredentials.setDeviceID(GId.asString(deviceRepresentation.getId()));
        } catch (Exception ex) {
        	throw new RuntimeException("Error creating agent for serial number" + serialNumber, ex);
        }
        
        return tixiCredentials;
    }

    public GId findGId(SerialNumber serialNumber) {
        return identityRepository.find(serialNumber);
    }

    private void executeMeasurementReqOperation(final MessageChannel<OperationRepresentation> messageChannel, final Subscription<GId> subscription,
            OperationRepresentation operation) {
    	MeasurementRequestOperation measurementRequest = operation.get(MeasurementRequestOperation.class);
    	if(acceptMeasurementRequest(measurementRequest)) {
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
}
