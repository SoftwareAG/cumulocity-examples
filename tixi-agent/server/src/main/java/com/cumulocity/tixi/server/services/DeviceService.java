package com.cumulocity.tixi.server.services;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.IsDevice;
import c8y.RequiredAvailability;
import c8y.SupportedOperations;

import com.cumulocity.agent.server.context.DeviceContext;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.context.DeviceCredentials;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.Agent;
import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.cumulocity.sdk.client.polling.PollingStrategy;
import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.TixiDeviceCredentails;

@Component
public class DeviceService {

    private static final long BOOTSTRAP_TIMEOUT_IN_SECONDS = 300L;

    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    private final DeviceCredentialsApi deviceCredentials;

    private final DeviceContextService contextService;

    private final InventoryRepository inventoryRepository;

    @Autowired
    public DeviceService(DeviceCredentialsApi deviceCredentials,
            DeviceContextService contextService, InventoryRepository inventoryRepository) {
        this.deviceCredentials = deviceCredentials;
        this.contextService = contextService;
        this.inventoryRepository = inventoryRepository;
    }

    public ManagedObjectRepresentation registerTixiAgent(final SerialNumber serialNumber) {
        ManagedObjectRepresentation managedObjectRepresentation  = findMoOrNull(serialNumber);
        if(managedObjectRepresentation != null) {
            return managedObjectRepresentation;
        }
        logger.debug("Create agent for serial: {}.", serialNumber);
        managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation.setName("c8y_TixiAgent_" + serialNumber.getValue());
        managedObjectRepresentation.setType("c8y_TixiAgent");
        managedObjectRepresentation.set(new Agent());
        managedObjectRepresentation.set(new IsDevice());
        managedObjectRepresentation.set(new RequiredAvailability(15));
        SupportedOperations supportedOperations = new SupportedOperations();
        supportedOperations.add("c8y_MeasurementRequestOperation");
        managedObjectRepresentation.set(supportedOperations);
        managedObjectRepresentation = inventoryRepository.save(managedObjectRepresentation, serialNumber);
        logger.debug("Agent for serial: {} created: {}.", serialNumber, managedObjectRepresentation);
        return managedObjectRepresentation;
    }

    public ManagedObjectRepresentation saveDeviceIfNotExists(String deviceId, String name, GId parentId) {
        SerialNumber serialNumber = new SerialNumber(deviceId + "_" + getTixiAgentIdFromContext()); 
        ManagedObjectRepresentation managedObjectRepresentation  = findMoOrNull(serialNumber);
        if(managedObjectRepresentation != null) {
            return managedObjectRepresentation;
        }
        logger.debug("Create device for serial: {} and parent: {}.", serialNumber, parentId);
        managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation.setName(name);
        managedObjectRepresentation.setType("tixi_device");
        managedObjectRepresentation = inventoryRepository.save(managedObjectRepresentation, serialNumber);
        inventoryRepository.bindToParent(parentId, managedObjectRepresentation.getId());
        logger.debug("Device for serial: {} created: {}.", serialNumber, managedObjectRepresentation);
        return managedObjectRepresentation;
    }

    public ManagedObjectRepresentation saveAgentIfNotExists(String agentId, String name, GId parentId) {
        SerialNumber serialNumber = new SerialNumber(agentId + "_" + getTixiAgentIdFromContext()); 
        ManagedObjectRepresentation managedObjectRepresentation  = findMoOrNull(serialNumber);
        if(managedObjectRepresentation != null) {
            return managedObjectRepresentation;
        }
        logger.debug("Create device for serial: {} and parent: {}.", serialNumber, parentId);
        managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation.setName(name);
        managedObjectRepresentation.setType(name);
        managedObjectRepresentation.set(new Agent());
        managedObjectRepresentation.set(new IsDevice());
        managedObjectRepresentation = inventoryRepository.save(managedObjectRepresentation, serialNumber);
        inventoryRepository.bindToParent(parentId, managedObjectRepresentation.getId());
        logger.debug("Agent for serial: {} created: {}.", serialNumber, managedObjectRepresentation);
        return managedObjectRepresentation;
    }
    
    private String getTixiAgentIdFromContext() {
        GId tixiAgentId = contextService.getCredentials().getDeviceId();
        if (tixiAgentId == null) {
            throw new RuntimeException("Tixi agent id cannot be null during device creation");
        }
        return tixiAgentId.getValue();
    }

    private ManagedObjectRepresentation findMoOrNull(ID agentSerial) {
        try {
            return inventoryRepository.findByExternalId(agentSerial);
        } catch (SDKException sdkEx) {
            return null;
        }
    }
    
    public ManagedObjectRepresentation find (GId id) {
        return inventoryRepository.findById(id);
    }
    
    public ManagedObjectRepresentation update(ManagedObjectRepresentation managedObjectRepresentation) {
        return inventoryRepository.save(managedObjectRepresentation);
    }
    
    public ManagedObjectRepresentation find(String deviceId) {
        SerialNumber serialNumber = new SerialNumber(deviceId + "_" + getTixiAgentIdFromContext());
        return findMoOrNull(serialNumber);
    }

    public TixiDeviceCredentails bootstrap(final SerialNumber serialNumber) {
        final DeviceCredentialsRepresentation credentials = deviceCredentials.pollCredentials(serialNumber.getValue(), new PollingStrategy(
                BOOTSTRAP_TIMEOUT_IN_SECONDS, SECONDS, asList(10L)));
        TixiDeviceCredentails tixiCredentials = TixiDeviceCredentails.from(credentials);

        try {
            ManagedObjectRepresentation deviceRepresentation = contextService.callWithinContext(
                    new DeviceContext(DeviceCredentials.from(credentials)), new Callable<ManagedObjectRepresentation>() {
                        @Override
                        public ManagedObjectRepresentation call() throws Exception {
                           return registerTixiAgent(serialNumber);
                        }

                    });
            tixiCredentials.setDeviceID(GId.asString(deviceRepresentation.getId()));
        } catch (Exception ex) {
            throw new RuntimeException("Error creating agent for serial number" + serialNumber, ex);
        }

        return tixiCredentials;
    }
    

}
