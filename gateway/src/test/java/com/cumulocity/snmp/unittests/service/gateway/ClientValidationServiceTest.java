package com.cumulocity.snmp.unittests.service.gateway;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.snmp.factory.platform.AlarmFactory;
import com.cumulocity.snmp.model.core.Alarms;
import com.cumulocity.snmp.model.core.ConfigEventType;
import com.cumulocity.snmp.model.core.Credentials;
import com.cumulocity.snmp.model.gateway.*;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.gateway.type.mapping.AlarmClearedEvent;
import com.cumulocity.snmp.model.gateway.type.mapping.AlarmCreatedEvent;
import com.cumulocity.snmp.model.notification.platform.PlatformRepresentationEvent;
import com.cumulocity.snmp.repository.AlarmRepository;
import com.cumulocity.snmp.repository.core.Repository;
import com.cumulocity.snmp.service.gateway.ClientValidationService;
import com.google.common.base.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientValidationServiceTest {

    @Mock
    private Repository<Gateway> gatewayRepository;

    @InjectMocks
    private ClientValidationService clientValidationService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private AlarmFactory alarmRepresentationFactory;

    @Mock
    private AlarmRepository alarmRepository;

    @Test
    public void shouldClearAlarmAndDoNotPublishEvent() {
        //given
        DeviceConfigSuccessEvent event = mock(DeviceConfigSuccessEvent.class);

        //when
        when(event.getGateway()).thenReturn(mock(Gateway.class));
        when(event.getDevice()).thenReturn(mock(Device.class));
        when(event.getGateway().getAlarms()).thenReturn(mock(Alarms.class));
        when(gatewayRepository.save(event.getGateway())).thenReturn(null);

        clientValidationService.clearAlarm(event);

        //then
        verify(gatewayRepository).save(event.getGateway());
        verify(eventPublisher, never()).publishEvent(any(AlarmClearedEvent.class));
    }

    @Test
    public void shouldClearAlarmsAndDoNotPublishEvent() {
        //given
        GatewayConfigSuccessEvent event = mock(GatewayConfigSuccessEvent.class);

        //when
        when(event.getGateway()).thenReturn(mock(Gateway.class));
        when(event.getGateway().getAlarms()).thenReturn(mock(Alarms.class));
        when(gatewayRepository.save(event.getGateway())).thenReturn(null);
        clientValidationService.clearAlarms(event);

        //then
        verify(gatewayRepository).save(event.getGateway());
        verify(eventPublisher, never()).publishEvent(any(AlarmClearedEvent.class));
    }

    @Test
    public void shouldStoreAlarmOnDeviceConfigErrorEvent() {
        DeviceConfigErrorEvent event = mock(DeviceConfigErrorEvent.class);

        //when
        when(event.getGateway()).thenReturn(mock(Gateway.class));
        when(event.getDevice()).thenReturn(mock(Device.class));
        when(event.getGateway().getAlarms()).thenReturn(mock(Alarms.class));
        when(alarmRepresentationFactory.apply(any(PlatformRepresentationEvent.class))).thenReturn(Optional.of(mock(AlarmRepresentation.class)));
        when(alarmRepository.create(any(Credentials.class), any(AlarmRepresentation.class))).thenReturn(Optional.of(mock(AlarmRepresentation.class)));

        clientValidationService.storeAlarm(event);

        //verify
        verify(eventPublisher).publishEvent(any(AlarmCreatedEvent.class));
    }

    @Test
    public void shouldStoreAlarmOnGatewayConfigErrorEvent() {
        GatewayConfigErrorEvent event = mock(GatewayConfigErrorEvent.class);

        //when
        when(event.getGateway()).thenReturn(mock(Gateway.class));
        when(event.getGateway().getAlarms()).thenReturn(mock(Alarms.class));
        when(event.getGateway().getAlarms().existsBySourceAndType(any(GId.class), any(ConfigEventType.class))).thenReturn(false);
        when(event.getType()).thenReturn(mock(ConfigEventType.class));
        when(alarmRepresentationFactory.apply(any(PlatformRepresentationEvent.class))).thenReturn(Optional.of(mock(AlarmRepresentation.class)));
        when(alarmRepository.create(any(Credentials.class), any(AlarmRepresentation.class))).thenReturn(Optional.of(mock(AlarmRepresentation.class)));

        clientValidationService.storeAlarm(event);

        //verify
        verify(eventPublisher).publishEvent(any(AlarmCreatedEvent.class));
    }

    @Test
    public void shouldStoreAlarmOnUnknownTrapOrDeviceEvent() {
        UnknownTrapOrDeviceEvent event = mock(UnknownTrapOrDeviceEvent.class);

        //when
        when(event.getGateway()).thenReturn(mock(Gateway.class));
        when(event.getGateway().getAlarms()).thenReturn(mock(Alarms.class));
        when(event.getGateway().getAlarms().existsBySourceAndType(any(GId.class), any(ConfigEventType.class))).thenReturn(false);
        when(event.getType()).thenReturn(mock(ConfigEventType.class));
        when(alarmRepresentationFactory.apply(any(PlatformRepresentationEvent.class))).thenReturn(Optional.of(mock(AlarmRepresentation.class)));
        when(alarmRepository.create(any(Credentials.class), any(AlarmRepresentation.class))).thenReturn(Optional.of(mock(AlarmRepresentation.class)));

        clientValidationService.storeAlarms(event);

        //verify
        verify(eventPublisher).publishEvent(any(AlarmCreatedEvent.class));
    }
}
