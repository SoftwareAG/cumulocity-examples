package com.cumulocity.snmp.unittests.service.gateway;

import com.cumulocity.snmp.model.core.Alarms;
import com.cumulocity.snmp.model.gateway.DeviceConfigSuccessEvent;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.GatewayConfigSuccessEvent;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.repository.core.Repository;
import com.cumulocity.snmp.service.gateway.ClientValidationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientValidationServiceTest {

    @Mock
    Repository<Gateway> gatewayRepository;

    @InjectMocks
    ClientValidationService clientValidationService;

    @Test
    public void shouldClearAlarmAndDonotPublishEvent() {
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
    }

    @Test
    public void shouldClearAlarmsAndDonotPublishEvent() {
        //given
        GatewayConfigSuccessEvent event = mock(GatewayConfigSuccessEvent.class);

        //when
        when(event.getGateway()).thenReturn(mock(Gateway.class));
        when(event.getGateway().getAlarms()).thenReturn(mock(Alarms.class));
        when(gatewayRepository.save(event.getGateway())).thenReturn(null);
        clientValidationService.clearAlarms(event);

        //then
        verify(gatewayRepository).save(event.getGateway());
    }
}
