package com.cumulocity.snmp.unittests.service.gateway;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.snmp.configuration.service.GatewayConfigurationProperties;
import com.cumulocity.snmp.factory.gateway.GatewayFactory;
import com.cumulocity.snmp.factory.platform.IdentityFactory;
import com.cumulocity.snmp.factory.platform.ManagedObjectFactory;
import com.cumulocity.snmp.factory.platform.ManagedObjectMapper;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.repository.DeviceCredentialsRepository;
import com.cumulocity.snmp.repository.IdentityRepository;
import com.cumulocity.snmp.repository.ManagedObjectRepository;
import com.cumulocity.snmp.repository.core.Repository;
import com.cumulocity.snmp.service.gateway.BootstrapService;
import com.cumulocity.snmp.utils.gateway.Scheduler;
import com.google.common.base.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.InvocationTargetException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BootstrapServiceTest {

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    Platform bootstrapPlatform;

    @Mock
    DeviceCredentialsRepository deviceCredentialsRepository;

    @Mock
    Repository<Gateway> gatewayRepository;

    @Mock
    ManagedObjectMapper managedObjectMapper;

    @Mock
    GatewayFactory gatewayFactory;

    @Mock
    IdentityRepository identityRepository;

    @Mock
    IdentityFactory identityFactory;

    @Mock
    InventoryApi inventoryApi;

    @Mock
    ManagedObjectRepository inventoryRepository;

    @Mock
    ManagedObjectFactory managedObjectFactory;

    @Mock
    GatewayConfigurationProperties properties;

    @Mock
    Scheduler scheduler;

    @Mock
    DeviceCredentialsApi deviceCredentialsApi;

    @Mock
    DeviceCredentialsRepresentation deviceCredentialsRepresentation;

    @Mock
    ExternalIDRepresentation existingExternalId;

    @Mock
    ManagedObjectRepresentation managedObjectRepresentation;

    @InjectMocks
    BootstrapService bootstrapService;


    @Test
    public void shouldTestSyncGatewaysWithGatewayConfig() throws InvocationTargetException, IllegalAccessException {
        Gateway gateway = mock(Gateway.class);
        ExternalIDRepresentation externalIDRepresentation = mock(ExternalIDRepresentation.class);

        //Given
        when(properties.getIdentifier()).thenReturn("snmp-agent");
        when(inventoryRepository.get(any(Gateway.class))).thenReturn(Optional.of(managedObjectRepresentation));
        when(gatewayFactory.create(any(Gateway.class), any(ManagedObjectRepresentation.class))).thenReturn(Optional.of(gateway));
        when(deviceCredentialsRepository.get(anyString())).thenReturn(Optional.of(deviceCredentialsRepresentation));
        when(identityRepository.get(any(DeviceCredentialsRepresentation.class), any(GId.class))).thenReturn(Optional.<ExternalIDRepresentation>absent());
        when(inventoryRepository.save(any(DeviceCredentialsRepresentation.class), any(ManagedObjectRepresentation.class))).thenReturn(Optional.of(managedObjectRepresentation));
        when(gatewayFactory.create(deviceCredentialsRepresentation, managedObjectRepresentation)).thenReturn(Optional.of(gateway));
        when(identityFactory.create(properties.getIdentifier(), managedObjectRepresentation)).thenReturn(existingExternalId);

        //When
        bootstrapService.syncGateways();

        //Then
        verify(gatewayRepository, atLeastOnce()).findAll();

    }

    private Gateway getGatewayInstance() {
        return Gateway.gateway()
                .tenant("tenant")
                .name("username")
                .password("password")
                .id(GId.asGId("10400"))
                .build();
    }
}