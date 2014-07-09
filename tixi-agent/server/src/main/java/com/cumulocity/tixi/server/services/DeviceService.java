package com.cumulocity.tixi.server.services;

import static com.cumulocity.model.idtype.GId.asString;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static jersey.repackaged.com.google.common.base.Throwables.propagate;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.IsDevice;

import com.cumulocity.agent.server.context.DeviceContext;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.context.DeviceCredentials;
import com.cumulocity.agent.server.repository.IdentityRepository;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.Agent;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.cumulocity.sdk.client.polling.PollingStrategy;
import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.TixiDeviceCredentails;

@Component
public class DeviceService {

    private final InventoryRepository inventoryRepository;

    private final IdentityRepository identityRepository;

    private final DeviceCredentialsApi deviceCredentials;

    private final DeviceContextService contextService;

    @Autowired
    public DeviceService(InventoryRepository inventoryRepository, IdentityRepository identityRepository,
            DeviceCredentialsApi deviceCredentials, DeviceContextService contextService) {
        this.inventoryRepository = inventoryRepository;
        this.identityRepository = identityRepository;
        this.deviceCredentials = deviceCredentials;
        this.contextService = contextService;
    }

    public TixiDeviceCredentails register(final SerialNumber serialNumber) {

        final DeviceCredentialsRepresentation credentials = deviceCredentials.pollCredentials(serialNumber.getValue(), new PollingStrategy(
                SECONDS, asList(10l)));

        final TixiDeviceCredentails tixiCredentials = TixiDeviceCredentails.from(credentials);
        try {
            ManagedObjectRepresentation deviceRepresentation = contextService.callWithinContext(
                    new DeviceContext(DeviceCredentials.from(credentials)), new Callable<ManagedObjectRepresentation>() {
                        @Override
                        public ManagedObjectRepresentation call() throws Exception {
                            return registerDeviceManagedObject(serialNumber);
                        }
                    });
            tixiCredentials.setDeviceID(asString(deviceRepresentation.getId()));
        } catch (Exception e) {
            propagate(e);
        }

        return tixiCredentials;
    }

    public ManagedObjectRepresentation registerDeviceManagedObject(final SerialNumber serialNumber) {
        ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();

        managedObjectRepresentation.set(new IsDevice());
        managedObjectRepresentation.set(new Agent());

        final ManagedObjectRepresentation managedObject = inventoryRepository.save(managedObjectRepresentation);

        identityRepository.save(managedObject.getId(), serialNumber);
        return managedObject;
    }

    public GId findGId(SerialNumber serialNumber) {
        return identityRepository.find(serialNumber);
    }

}
