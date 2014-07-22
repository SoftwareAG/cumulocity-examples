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

import c8y.IsDevice;
import c8y.MeasurementRequestOperation;

import com.cumulocity.agent.server.context.DeviceContext;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.context.DeviceCredentials;
import com.cumulocity.agent.server.repository.DeviceControlRepository;
import com.cumulocity.agent.server.repository.IdentityRepository;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.Agent;
import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.cumulocity.sdk.client.devicecontrol.OperationFilter;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;
import com.cumulocity.sdk.client.polling.PollingStrategy;
import com.cumulocity.tixi.server.model.Operations;
import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.TixiDeviceCredentails;

@Component
public class DeviceService {

    private static final long BOOTSTRAP_TIMEOUT_IN_SECONDS = 300L;

    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    private final IdentityRepository identityRepository;

    private final DeviceCredentialsApi deviceCredentials;

    private final DeviceContextService contextService;

    private final InventoryRepository inventoryRepository;

    @Autowired
    public DeviceService(IdentityRepository identityRepository, DeviceCredentialsApi deviceCredentials,
            DeviceContextService contextService, InventoryRepository inventoryRepository) {
        this.identityRepository = identityRepository;
        this.deviceCredentials = deviceCredentials;
        this.contextService = contextService;
        this.inventoryRepository = inventoryRepository;
    }

    public TixiDeviceCredentails register(final SerialNumber serialNumber) {

        final DeviceCredentialsRepresentation credentials = deviceCredentials.pollCredentials(serialNumber.getValue(), new PollingStrategy(
                BOOTSTRAP_TIMEOUT_IN_SECONDS, SECONDS, asList(10L)));
        TixiDeviceCredentails tixiCredentials = TixiDeviceCredentails.from(credentials);

        try {
            ManagedObjectRepresentation deviceRepresentation = contextService.callWithinContext(
                    new DeviceContext(DeviceCredentials.from(credentials)), new Callable<ManagedObjectRepresentation>() {
                        @Override
                        public ManagedObjectRepresentation call() throws Exception {
                            return saveAgentIfNotExists("c8y_TixiAgent", "c8y_TixiAgent_" + serialNumber.getValue(),
                                    serialNumber, null);
                        }
                    });
            tixiCredentials.setDeviceID(GId.asString(deviceRepresentation.getId()));
        } catch (Exception ex) {
            throw new RuntimeException("Error creating agent for serial number" + serialNumber, ex);
        }

        return tixiCredentials;
    }
    
    public ManagedObjectRepresentation saveDeviceIfNotExists(ID id, String name, GId parentId) {
        ManagedObjectRepresentation managedObjectRepresentation  = findMoOrNull(id);
        if(managedObjectRepresentation != null) {
            return managedObjectRepresentation;
        }
        logger.debug("Create device for serial: {} and agent: {}.", id, parentId);
        managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation.setName(name);
        managedObjectRepresentation.setType("tixi_device");
        managedObjectRepresentation = inventoryRepository.save(managedObjectRepresentation, id);
        inventoryRepository. bindToParent(parentId, managedObjectRepresentation.getId());
        logger.debug("Device for serial: {} created: {}.", id, managedObjectRepresentation);
        return managedObjectRepresentation;
    }
    
    public ManagedObjectRepresentation saveAgentIfNotExists(String type, String name, SerialNumber serial, GId parentId) {
        ManagedObjectRepresentation managedObjectRepresentation  = findMoOrNull(serial);
        if(managedObjectRepresentation != null) {
            return managedObjectRepresentation;
        }
        logger.debug("Create agent for serial: {}.", serial);
        managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation.set(new IsDevice());
        managedObjectRepresentation.setName(name);
        managedObjectRepresentation.set(new Agent());
        managedObjectRepresentation.setType(type);
        managedObjectRepresentation = inventoryRepository.save(managedObjectRepresentation, serial);
        if(parentId != null) {
            inventoryRepository.bindToParent(parentId, managedObjectRepresentation.getId());
        }
        logger.debug("Agent for serial: {} created: {}.", serial, managedObjectRepresentation);
        return managedObjectRepresentation;
    }

    private ManagedObjectRepresentation findMoOrNull(ID agentSerial) {
        try {
            return inventoryRepository.findByExternalId(agentSerial);
        } catch (SDKException sdkEx) {
            return null;
        }
    }
    
    public GId findGId(SerialNumber serialNumber) {
        return identityRepository.find(serialNumber);
    }
    
    public ManagedObjectRepresentation find (GId id ) {
        return inventoryRepository.findById(id);
    }
    
    public ManagedObjectRepresentation update( ManagedObjectRepresentation managedObjectRepresentation) {
        return inventoryRepository.save(managedObjectRepresentation);
    }
    
    public ManagedObjectRepresentation find(SerialNumber serial) {
        return findMoOrNull(serial);
    }
    

}
