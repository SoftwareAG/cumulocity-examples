package com.cumulocity.snmp.service.gateway;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.notification.Subscriber;
import com.cumulocity.snmp.model.device.DeviceAddedEvent;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.notification.Notifications;
import com.cumulocity.snmp.model.notification.platform.Subscriptions;
import com.cumulocity.snmp.model.type.DeviceType;
import com.cumulocity.snmp.repository.DeviceTypeInventoryRepository;
import com.cumulocity.snmp.repository.core.Repository;
import com.cumulocity.snmp.repository.platform.PlatformProvider;
import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeviceTypeServiceTest {

    @Mock
    DeviceTypeInventoryRepository deviceTypeInventoryRepository;

    @Mock
    Repository<DeviceType> deviceTypePeristedRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    Subscriptions subscribers;

    @Mock
    PlatformProvider platformProvider;

    @Mock
    Notifications notifications;

    @InjectMocks
    DeviceTypeService deviceTypeService;

    private GId gId;
    private DeviceAddedEvent event;
    private Optional<DeviceType> newDeviceTypeOptional;
    private Optional<DeviceType> previousDeviceTypeOptional;
    private DeviceType deviceType;
    private PlatformParameters platform;

    @Before
    public void init() {
        gId = mock(GId.class);
        event = mock(DeviceAddedEvent.class);
        newDeviceTypeOptional = mock(Optional.class);
        previousDeviceTypeOptional = mock(Optional.class);
        deviceType = mock(DeviceType.class);
        platform = mock(PlatformParameters.class);
    }

    @Test
    public void shouldAddDeviceType() throws ExecutionException {

//        given
        when(deviceType.getId()).thenReturn(gId);
        when(event.getGateway()).thenReturn(mock(Gateway.class));
        when(event.getDevice()).thenReturn(mock(Device.class));
        when(deviceTypeInventoryRepository.get(event.getGateway(), event.getDevice().getDeviceType())).thenReturn(newDeviceTypeOptional);
        when(newDeviceTypeOptional.isPresent()).thenReturn(true);
        when(newDeviceTypeOptional.get()).thenReturn(deviceType);
        when(deviceTypePeristedRepository.get(newDeviceTypeOptional.get().getId())).thenReturn(previousDeviceTypeOptional);
        when(deviceTypePeristedRepository.save(deviceType)).thenReturn(deviceType);
        when(platformProvider.getPlatformProperties(event.getGateway())).thenReturn(platform);
        doNothing().when(subscribers).add(any(GId.class), any(Subscriber.class));

//        when
        deviceTypeService.addDeviceType(event);

//        then
        verify(deviceTypeInventoryRepository, times(1)).get(event.getGateway(), event.getDevice().getDeviceType());
        verify(subscribers, times(1)).disconnect(gId);
        verify(deviceTypePeristedRepository, times(1)).save(deviceType);
        verify(eventPublisher, times(0)).publishEvent(event);
    }
}
