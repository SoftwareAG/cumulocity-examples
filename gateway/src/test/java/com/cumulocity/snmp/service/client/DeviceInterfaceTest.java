package com.cumulocity.snmp.service.client;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.snmp.configuration.service.SNMPConfigurationProperties;
import com.cumulocity.snmp.model.device.DeviceAddedEvent;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.UnknownTrapOrDeviceEvent;
import com.cumulocity.snmp.model.gateway.device.Device;
import org.junit.Assert;
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

import static com.cumulocity.model.idtype.GId.asGId;
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

    @Test
    public void shouldSendV3Trap() {

        when(config.getAddress()).thenReturn("udp:127.0.0.1");
        when(config.getListenerPort()).thenReturn(6690);
        when(config.getCommunityTarget()).thenReturn("public");
        deviceInterface.init();
        deviceInterface.addSnmpV3Credentials(new DeviceAddedEvent(getGatewayInstance(), getDevice()));

        CommandResponderEvent event = mock(CommandResponderEvent.class);
        PDU pdu = mock(PDU.class);
        Vector vector = mock(Vector.class);

        when(event.getPDU()).thenReturn(pdu);
        when(pdu.getVariableBindings()).thenReturn(vector);

        deviceInterface.processPdu(event);

        verify(event, atLeastOnce()).getPDU();
        verify(pdu, atLeastOnce()).getVariableBindings();
    }

    public void shouldReturnEmptyOidMappingsForGivenIpAddress() {
        String ipAddress = "127.0.0.1";
        Map<String, PduListener> mappings = deviceInterface.removeOidMappings(ipAddress);

        Assert.assertTrue(mappings.isEmpty());
    }

    private Gateway getGatewayInstance() {
        return Gateway.gateway()
                .tenant("tenant")
                .name("username")
                .password("password")
                .id(GId.asGId("10400"))
                .build();
    }

    private Device getDevice() {
        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress("192.168.1.1");
        device.setDeviceType(asGId("15257"));
        device.setPort(6691);
        device.setSnmpVersion(3);
        device.setUsername("dummyUser");
        device.setSecurityLevel(3);
        device.setAuthProtocol(1);
        device.setAuthProtocolPassword("dummyAuthPassword");
        device.setPrivacyProtocol(1);
        device.setPrivacyProtocolPassword("dummyPrivPassword");
        device.setEngineId("12345");

        return device;
    }
}
