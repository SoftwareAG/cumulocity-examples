package com.cumulocity.tixi.server.services;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.repository.IdentityRepository;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.cumulocity.sdk.client.polling.PollingStrategy;
import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.TixiDeviceCredentails;

@Component
public class DeviceService {

    private final IdentityRepository identityRepository;

    private final DeviceCredentialsApi deviceCredentials;

    @Autowired
    public DeviceService(InventoryRepository inventoryRepository, IdentityRepository identityRepository,
            DeviceCredentialsApi deviceCredentials, DeviceContextService contextService) {
        this.identityRepository = identityRepository;
        this.deviceCredentials = deviceCredentials;
    }

    public TixiDeviceCredentails register(final SerialNumber serialNumber) {

        final DeviceCredentialsRepresentation credentials = deviceCredentials.pollCredentials(serialNumber.getValue(), new PollingStrategy(
                SECONDS, asList(10l)));

        final TixiDeviceCredentails tixiCredentials = TixiDeviceCredentails.from(credentials);
        return tixiCredentials;
    }

    public GId findGId(SerialNumber serialNumber) {
        return identityRepository.find(serialNumber);
    }

}
