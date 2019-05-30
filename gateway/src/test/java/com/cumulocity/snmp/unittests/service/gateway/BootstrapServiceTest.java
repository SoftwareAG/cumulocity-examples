package com.cumulocity.snmp.unittests.service.gateway;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryApiImpl;
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
import org.mockito.Spy;
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

    @Spy
    Platform bootstrapPlatform = new PlatformImpl();

    @Spy
    DeviceCredentialsRepository deviceCredentialsRepository;

    @Mock
    Repository<Gateway> gatewayRepository;

    @Mock
    Optional<Gateway> gatewayOptional;

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

    @Spy
    ManagedObjectRepository inventoryRepository;

    @Mock
    ManagedObjectFactory managedObjectFactory;

    @Mock
    GatewayConfigurationProperties properties;

    @Mock
    Scheduler scheduler;

    @Mock
    Optional<DeviceCredentialsRepresentation> deviceCredentialsRepresentationOptional;

    @Mock
    DeviceCredentialsApi deviceCredentialsApi;

    @Mock
    DeviceCredentialsRepresentation deviceCredentialsRepresentation;

    @Mock
    Optional<ExternalIDRepresentation> existingExternalIdOptional;

    @Mock
    ExternalIDRepresentation existingExternalId;

    @Mock
    Optional<ManagedObjectRepresentation> managedObjectRepresentationOptional;

    @Mock
    ManagedObjectRepresentation managedObjectRepresentation;

    @InjectMocks
    BootstrapService bootstrapService;


    @Test
    public void shouldTestSyncGatewaysWithGatewayConfig() throws InvocationTargetException, IllegalAccessException {

        //Given
        when(properties.getIdentifier()).thenReturn("snmp-agent");
        deviceCredentialsRepository.setBootstrapPlatform(bootstrapPlatform);
        doReturn(deviceCredentialsApi).when(bootstrapPlatform).getDeviceCredentialsApi();
        when(deviceCredentialsApi.pollCredentials("snmp-agent")).thenReturn(deviceCredentialsRepresentation);
        when(identityRepository.get(any(DeviceCredentialsRepresentation.class), any(ID.class))).thenReturn(existingExternalIdOptional);
        when(managedObjectFactory.create(anyString())).thenReturn(managedObjectRepresentation);
        inventoryApi = mock(InventoryApiImpl.class);
        inventoryRepository.setInventory(inventoryApi);
        when(inventoryApi.create(any(ManagedObjectRepresentation.class))).thenReturn(managedObjectRepresentation);
        when(gatewayFactory.create(deviceCredentialsRepresentation, managedObjectRepresentation)).thenReturn(gatewayOptional);
        when(gatewayOptional.isPresent()).thenReturn(true);
        when(gatewayOptional.get()).thenReturn(getGatewayInstance());
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