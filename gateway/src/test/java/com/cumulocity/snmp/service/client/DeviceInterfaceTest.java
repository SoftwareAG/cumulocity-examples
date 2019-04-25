package com.cumulocity.snmp.service.client;

import com.cumulocity.snmp.configuration.service.SNMPConfigurationProperties;
import com.cumulocity.snmp.model.gateway.UnknownTrapOrDeviceEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.UdpAddress;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeviceInterfaceTest {

    @InjectMocks
    DeviceInterface deviceInterface;

    @Mock
    SNMPConfigurationProperties config;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Test
    public void shouldSuccessfullyInitiateTrapListener() throws IOException {
        UdpAddress udpAddress = mock(UdpAddress.class);

        when(config.getCommunityTarget()).thenReturn("public");

        deviceInterface.listen(udpAddress);
    }

    @Test
    public void shouldReturnAsPduIsNull() {
        CommandResponderEvent event = mock(CommandResponderEvent.class);

        when(event.getPDU()).thenReturn(null);

        deviceInterface.processPdu(event);

        verify(event, atLeastOnce()).getPDU();
    }

    @Test
    public void shouldReturnAsVariableBindingIsNull() {
        CommandResponderEvent event = mock(CommandResponderEvent.class);
        PDU pdu = mock(PDU.class);

        when(event.getPDU()).thenReturn(pdu);

        deviceInterface.processPdu(event);

        verify(event, atLeastOnce()).getPDU();
        verify(pdu, atLeastOnce()).getVariableBindings();
    }

    @Test
    public void shouldReturnAsNoVariableBindings() {
        CommandResponderEvent event = mock(CommandResponderEvent.class);
        PDU pdu = mock(PDU.class);
        Vector vector = mock(Vector.class);

        when(event.getPDU()).thenReturn(pdu);
        when(pdu.getVariableBindings()).thenReturn(vector);

        deviceInterface.processPdu(event);

        verify(event, atLeastOnce()).getPDU();
        verify(pdu, atLeastOnce()).getVariableBindings();
    }

    @Test
    public void shouldPublishUnknownTrapRecievedEventWhenReceivedTrapHostIsUnknown() {
        CommandResponderEvent event = mock(CommandResponderEvent.class);
        PDU pdu = mock(PDU.class);
        Vector vector = mock(Vector.class);
        Address address = mock(Address.class);

        when(event.getPDU()).thenReturn(pdu);
        when(pdu.getVariableBindings()).thenReturn(vector);
        when(pdu.getVariableBindings().size()).thenReturn(1);
        when(event.getPeerAddress()).thenReturn(address);
        when(event.getPeerAddress().toString()).thenReturn("localhost/162");

        deviceInterface.processPdu(event);

        verify(event, atLeastOnce()).getPDU();
        verify(pdu, atLeastOnce()).getVariableBindings();
        verify(eventPublisher, atLeastOnce()).publishEvent(any(UnknownTrapOrDeviceEvent.class));
    }

    @Test
    public void shouldDoNothingWhenReceivedTrapHasNoConfiguration() {
        CommandResponderEvent event = mock(CommandResponderEvent.class);
        PDU pdu = mock(PDU.class);
        Vector vector = mock(Vector.class);
        Address address = mock(Address.class);
        Map<String, PduListener> listenerMap = mock(HashMap.class);
        Map<String, Map<String, PduListener>> map = new HashMap<>();
        map.put("localhost", listenerMap);
        Iterator variableBindingIterator = mock(Iterator.class);

        when(event.getPDU()).thenReturn(pdu);
        when(pdu.getVariableBindings()).thenReturn(vector);
        when(pdu.getVariableBindings().size()).thenReturn(1);
        when(event.getPeerAddress()).thenReturn(address);
        when(event.getPeerAddress().toString()).thenReturn("localhost/162");
        when(pdu.getVariableBindings().iterator()).thenReturn(variableBindingIterator);

        deviceInterface.subscribe(map);
        deviceInterface.processPdu(event);

        verify(event, atLeastOnce()).getPDU();
        verify(pdu, atLeastOnce()).getVariableBindings();
    }
}
