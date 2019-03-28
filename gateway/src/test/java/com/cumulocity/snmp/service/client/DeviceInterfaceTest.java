package com.cumulocity.snmp.service.client;

import com.cumulocity.snmp.configuration.service.SNMPConfigurationProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.snmp4j.PDU;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
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
    public void ShouldReturnResponseAsNullForPollingIncorrectDevicePort() throws IOException {
        String oId = "1.3.6.1.2.1.1.7.0";
        String ipAddress = "localhost";

        final PDU pdu = mock(PDU.class);

        Vector variableBindings = mock(Vector.class);
        VariableBinding variableBinding = mock(VariableBinding.class);
        Variable variable = mock(Variable.class);
        PduListener pduListener = mock(PduListener.class);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");
        when(config.getPollingPort()).thenReturn("123");
        doNothing().when(pduListener).onPduReceived(any(PDU.class));

        deviceInterface.initiatePolling(oId, ipAddress, pduListener);

        verify(pduListener, never()).onPduReceived(any(PDU.class));
    }

    @Test
    public void ShouldReturnResponseAsNullForPollingIncorrectOid() throws IOException {
        String oId = "1.6.1.2.1.1.7.0";
        String ipAddress = "localhost";

        final PDU pdu = mock(PDU.class);
        Vector variableBindings = mock(Vector.class);
        VariableBinding variableBinding = mock(VariableBinding.class);
        Variable variable = mock(Variable.class);
        PduListener pduListener = mock(PduListener.class);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");
        when(config.getPollingPort()).thenReturn("6672");

        deviceInterface.initiatePolling(oId, ipAddress, pduListener);

        verify(pduListener, never()).onPduReceived(pdu);
    }
}
