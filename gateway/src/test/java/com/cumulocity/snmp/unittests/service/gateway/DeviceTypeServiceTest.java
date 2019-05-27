package com.cumulocity.snmp.unittests.service.gateway;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.cep.notification.InventoryRealtimeDeleteAwareNotificationsSubscriber;
import com.cumulocity.snmp.factory.gateway.DeviceFactory;
import com.cumulocity.snmp.model.core.Credentials;
import com.cumulocity.snmp.model.device.DeviceAddedEvent;
import com.cumulocity.snmp.model.device.DeviceUpdatedEvent;
import com.cumulocity.snmp.model.gateway.DeviceTypeAddedEvent;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.notification.Notifications;
import com.cumulocity.snmp.model.notification.platform.ManagedObjectListener;
import com.cumulocity.snmp.model.type.DeviceType;
import com.cumulocity.snmp.repository.DeviceTypeInventoryRepository;
import com.cumulocity.snmp.repository.ManagedObjectRepository;
import com.cumulocity.snmp.repository.core.Repository;
import com.cumulocity.snmp.repository.platform.PlatformProvider;
import com.cumulocity.snmp.service.gateway.DeviceTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.util.concurrent.ExecutionException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeviceTypeServiceTest {

    @InjectMocks
    private DeviceTypeService deviceTypeService;

    @Mock
    private Repository<DeviceType> deviceTypePeristedRepository;

    @Mock
    private DeviceTypeInventoryRepository deviceTypeInventoryRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private PlatformProvider platformProvider;

    @Mock
    private Notifications notifications;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ManagedObjectRepository managedObjectRepository;

    @Mock
    private DeviceFactory deviceFactory;

    private Gateway gateway;

    private Device device;

    private GId gId;

    private DeviceType newDeviceTypeOptional;

    private DeviceType previousDeviceTypeOptional;

    @Before
    public void init() {
        gateway = mock(Gateway.class);
        device = mock(Device.class);
        gId = mock(GId.class);
        newDeviceTypeOptional = mock(DeviceType.class);
        previousDeviceTypeOptional = mock(DeviceType.class);

        when(device.getDeviceType()).thenReturn(gId);
        when(deviceTypeInventoryRepository.get(gateway, gId)).thenReturn(Optional.of(newDeviceTypeOptional));
        when(newDeviceTypeOptional.getId()).thenReturn(gId);
        when(deviceTypePeristedRepository.get(newDeviceTypeOptional.getId())).thenReturn(Optional.of(previousDeviceTypeOptional));
        when(deviceTypePeristedRepository.save(newDeviceTypeOptional)).thenReturn(newDeviceTypeOptional);
        when(notifications.subscribeInventory(any(PlatformParameters.class), any(GId.class), any(ManagedObjectListener.class)))
                .thenReturn(mock(InventoryRealtimeDeleteAwareNotificationsSubscriber.class));

    }

    @Test
    public void shouldAddDeviceTypeOnDeviceAddedEvent() throws ExecutionException {
        DeviceAddedEvent event = mock(DeviceAddedEvent.class);
        when(event.getGateway()).thenReturn(gateway);
        when(event.getDevice()).thenReturn(device);

        deviceTypeService.addDeviceType(event);

        verify(eventPublisher).publishEvent(any(DeviceTypeAddedEvent.class));
    }

    @Test
    public void shouldAddDeviceTypeOnDeviceUpdatedEvent() throws ExecutionException {
        DeviceUpdatedEvent event = mock(DeviceUpdatedEvent.class);
        ManagedObjectRepresentation representation = mock(ManagedObjectRepresentation.class);

        when(event.getGateway()).thenReturn(gateway);
        when(event.getDeviceId()).thenReturn(gId);
        when(managedObjectRepository.get(gateway, gId)).thenReturn(Optional.of(representation));
        when(deviceFactory.convert(representation)).thenReturn(Optional.of(device));

        deviceTypeService.updateDeviceType(event);

        verify(platformProvider).getPlatformProperties(any(Credentials.class));
    }
}
