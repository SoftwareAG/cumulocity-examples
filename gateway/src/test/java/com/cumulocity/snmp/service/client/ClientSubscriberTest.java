package com.cumulocity.snmp.service.client;

import com.cumulocity.snmp.model.gateway.*;
import com.cumulocity.snmp.model.gateway.client.ClientDataChangedEvent;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.gateway.type.core.Register;
import com.cumulocity.snmp.model.type.DeviceType;
import com.cumulocity.snmp.repository.core.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientSubscriberTest {

    @Mock
    Repository<DeviceType> deviceTypeRepository;

    @Mock
    Repository<Device> deviceRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    DeviceInterface deviceInterface;

    @Mock
    Map<Device, DeviceType> devicePollingData;

    @InjectMocks
    ClientSubscriber clientSubscriber;

    @Test
    public void shouldRefreshSubscriptionOnDeviceTypeAddedEvent() {
        DeviceTypeAddedEvent event = mock(DeviceTypeAddedEvent.class);
        Gateway gateway = mock(Gateway.class);
        Device device = mock(Device.class);
        DeviceConfigErrorEvent deviceConfigErrorEvent = mock(DeviceConfigErrorEvent.class);
        DeviceType deviceType = mock(DeviceType.class);
        List<Register> registers = new ArrayList<>();
        Register register = mock(Register.class);
        registers.add(register);

        when(event.getDevice()).thenReturn(device);
        when(event.getDeviceType()).thenReturn(deviceType);
        when(event.getGateway()).thenReturn(gateway);
        when(deviceType.getRegisters()).thenReturn(registers);
        when(device.getIpAddress()).thenReturn("localhost");

        doNothing().when(eventPublisher).publishEvent(deviceConfigErrorEvent);
        doNothing().when(deviceInterface).setGateway(gateway);

        clientSubscriber.refreshSubscription(event);

        verify(eventPublisher).publishEvent(any(GatewayConfigSuccessEvent.class));
        verify(eventPublisher).publishEvent(any(ClientDataChangedEvent.class));
        verify(deviceInterface).subscribe(any(HashMap.class));
    }

    @Test
    public void shouldRefreshSubscriptionOnDeviceTypeUpdatedEvent() {
        DeviceTypeUpdatedEvent event = mock(DeviceTypeUpdatedEvent.class);
        Gateway gateway = mock(Gateway.class);
        Device device = mock(Device.class);
        DeviceConfigErrorEvent deviceConfigErrorEvent = mock(DeviceConfigErrorEvent.class);
        DeviceType deviceType = mock(DeviceType.class);
        List<Register> registers = new ArrayList<>();
        Register register = mock(Register.class);
        registers.add(register);

        when(event.getDevice()).thenReturn(device);
        when(event.getDeviceType()).thenReturn(deviceType);
        when(event.getGateway()).thenReturn(gateway);
        when(deviceType.getRegisters()).thenReturn(registers);
        when(device.getIpAddress()).thenReturn("localhost");

        doNothing().when(eventPublisher).publishEvent(deviceConfigErrorEvent);
        doNothing().when(deviceInterface).setGateway(gateway);

        clientSubscriber.refreshSubscription(event);

    }
}
