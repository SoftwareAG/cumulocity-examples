package com.cumulocity.snmp.service.client;

import com.cumulocity.snmp.configuration.service.SNMPConfigurationProperties;
import com.cumulocity.snmp.model.gateway.device.Device;
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

import static com.cumulocity.model.idtype.GId.asGId;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DevicePolingServiceTest {

    @InjectMocks
    DevicePollingService pollingService;

    @Mock
    SNMPConfigurationProperties config;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Test
    public void ShouldReturnResponseAsNullForPollingIncorrectDevicePortUsingSnmpVersion1() throws IOException {
        String oId = "1.3.6.1.2.1.1.7.0";
        String ipAddress = "localhost";
        int port = 123;
        int snmpVersion = 0; // version 1
        final PDU pdu = mock(PDU.class);

        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress(ipAddress);
        device.setDeviceType(asGId("15257"));
        device.setPort(port);
        device.setSnmpVersion(snmpVersion);
        Vector variableBindings = mock(Vector.class);
        VariableBinding variableBinding = mock(VariableBinding.class);
        Variable variable = mock(Variable.class);
        PduListener pduListener = mock(PduListener.class);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");
        doNothing().when(pduListener).onPduReceived(any(PDU.class));

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onPduReceived(any(PDU.class));
    }

    @Test
    public void ShouldReturnResponseAsNullForPollingIncorrectOidUsingSnmpVersion1() throws IOException {
        String oId = "1.6.1.2.1.1.7.0";
        String ipAddress = "localhost";
        int port = 123;
        int snmpVersion = 0; // version 1

        final PDU pdu = mock(PDU.class);
        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress(ipAddress);
        device.setDeviceType(asGId("15257"));
        device.setPort(port);
        device.setSnmpVersion(snmpVersion);
        Vector variableBindings = mock(Vector.class);
        VariableBinding variableBinding = mock(VariableBinding.class);
        Variable variable = mock(Variable.class);
        PduListener pduListener = mock(PduListener.class);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onPduReceived(pdu);
    }

    @Test
    public void ShouldReturnResponseAsNullForPollingIncorrectDevicePortUsingSnmpVersion2c() throws IOException {
        String oId = "1.3.6.1.2.1.1.7.0";
        String ipAddress = "localhost";
        int port = 123;
        int snmpVersion = 1; // version 2c
        final PDU pdu = mock(PDU.class);

        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress(ipAddress);
        device.setDeviceType(asGId("15257"));
        device.setPort(port);
        device.setSnmpVersion(snmpVersion);
        Vector variableBindings = mock(Vector.class);
        VariableBinding variableBinding = mock(VariableBinding.class);
        Variable variable = mock(Variable.class);
        PduListener pduListener = mock(PduListener.class);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");
        doNothing().when(pduListener).onPduReceived(any(PDU.class));

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onPduReceived(any(PDU.class));
    }

    @Test
    public void ShouldReturnResponseAsNullForPollingIncorrectOidUsingSnmpVersion2c() throws IOException {
        String oId = "1.6.1.2.1.1.7.0";
        String ipAddress = "localhost";
        int port = 123;
        int snmpVersion = 1; // version 2c

        final PDU pdu = mock(PDU.class);
        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress(ipAddress);
        device.setDeviceType(asGId("15257"));
        device.setPort(port);
        device.setSnmpVersion(snmpVersion);
        Vector variableBindings = mock(Vector.class);
        VariableBinding variableBinding = mock(VariableBinding.class);
        Variable variable = mock(Variable.class);
        PduListener pduListener = mock(PduListener.class);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onPduReceived(pdu);
    }

    @Test
    public void ShouldReturnNullResponseAsNoUsernameProvidedUsingSnmpVersion3() throws IOException {
        String oId = "1.6.1.2.1.1.7.0";
        String ipAddress = "localhost";
        int port = 123;
        int snmpVersion = 3; // version 3

        final PDU pdu = mock(PDU.class);
        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress(ipAddress);
        device.setDeviceType(asGId("15257"));
        device.setPort(port);
        device.setSnmpVersion(snmpVersion);
        Vector variableBindings = mock(Vector.class);
        VariableBinding variableBinding = mock(VariableBinding.class);
        Variable variable = mock(Variable.class);
        PduListener pduListener = mock(PduListener.class);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onPduReceived(pdu);
    }

    @Test
    public void ShouldReturnNullResponseAsNoSecurityLevelProvidedUsingSnmpVersion3() throws IOException {
        String oId = "1.6.1.2.1.1.7.0";
        String ipAddress = "localhost";
        int port = 123;
        int snmpVersion = 3; // version 3

        final PDU pdu = mock(PDU.class);
        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress(ipAddress);
        device.setDeviceType(asGId("15257"));
        device.setPort(port);
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        Vector variableBindings = mock(Vector.class);
        VariableBinding variableBinding = mock(VariableBinding.class);
        Variable variable = mock(Variable.class);
        PduListener pduListener = mock(PduListener.class);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onPduReceived(pdu);
    }

    @Test
    public void ShouldReturnNullResponseAsUndefinedSecurityLevelProvidedUsingSnmpVersion3() throws IOException {
        String oId = "1.6.1.2.1.1.7.0";
        String ipAddress = "localhost";
        int port = 123;
        int snmpVersion = 3; // version 3

        final PDU pdu = mock(PDU.class);
        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress(ipAddress);
        device.setDeviceType(asGId("15257"));
        device.setPort(port);
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        device.setSecurityLevel(0);
        Vector variableBindings = mock(Vector.class);
        VariableBinding variableBinding = mock(VariableBinding.class);
        Variable variable = mock(Variable.class);
        PduListener pduListener = mock(PduListener.class);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onPduReceived(pdu);
    }

    @Test
    public void ShouldReturnNullResponseWithNoAuthNoPrivUsingSnmpVersion3() throws IOException {
        String oId = "1.6.1.2.1.1.7.0";
        String ipAddress = "localhost";
        int port = 123;
        int snmpVersion = 3; // version 3

        final PDU pdu = mock(PDU.class);
        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress(ipAddress);
        device.setDeviceType(asGId("15257"));
        device.setPort(port);
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        device.setSecurityLevel(1);
        Vector variableBindings = mock(Vector.class);
        VariableBinding variableBinding = mock(VariableBinding.class);
        Variable variable = mock(Variable.class);
        PduListener pduListener = mock(PduListener.class);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onPduReceived(pdu);
    }

    @Test
    public void ShouldReturnNullResponseWithAuthNoPrivInvalidAuthProtocolUsingSnmpVersion3() throws IOException {
        String oId = "1.6.1.2.1.1.7.0";
        String ipAddress = "localhost";
        int port = 123;
        int snmpVersion = 3; // version 3

        final PDU pdu = mock(PDU.class);
        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress(ipAddress);
        device.setDeviceType(asGId("15257"));
        device.setPort(port);
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        device.setSecurityLevel(2);
        device.setAuthProtocol(0);
        device.setAuthProtocolPassword("authpassword");
        Vector variableBindings = mock(Vector.class);
        VariableBinding variableBinding = mock(VariableBinding.class);
        Variable variable = mock(Variable.class);
        PduListener pduListener = mock(PduListener.class);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onPduReceived(pdu);
    }

    @Test
    public void ShouldReturnNullResponseWithAuthNoPrivInvalidAuthProtocolInvalidPasswordUsingSnmpVersion3() throws IOException {
        String oId = "1.6.1.2.1.1.7.0";
        String ipAddress = "localhost";
        int port = 123;
        int snmpVersion = 3; // version 3

        final PDU pdu = mock(PDU.class);
        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress(ipAddress);
        device.setDeviceType(asGId("15257"));
        device.setPort(port);
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        device.setSecurityLevel(2);
        device.setAuthProtocol(1);
        device.setAuthProtocolPassword("auth123");
        Vector variableBindings = mock(Vector.class);
        VariableBinding variableBinding = mock(VariableBinding.class);
        Variable variable = mock(Variable.class);
        PduListener pduListener = mock(PduListener.class);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onPduReceived(pdu);
    }

    @Test
    public void ShouldReturnNullResponseWithAuthNoPrivInvalidAuthProtocolInvalidPortNumberUsingSnmpVersion3() throws IOException {
        String oId = "1.6.1.2.1.1.7.0";
        String ipAddress = "localhost";
        int port = 123;
        int snmpVersion = 3; // version 3

        final PDU pdu = mock(PDU.class);
        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress(ipAddress);
        device.setDeviceType(asGId("15257"));
        device.setPort(port);
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        device.setSecurityLevel(2);
        device.setAuthProtocol(2);
        device.setAuthProtocolPassword("authpass");
        Vector variableBindings = mock(Vector.class);
        VariableBinding variableBinding = mock(VariableBinding.class);
        Variable variable = mock(Variable.class);
        PduListener pduListener = mock(PduListener.class);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onPduReceived(pdu);
    }

    @Test
    public void ShouldReturnNullResponseWithAuthNoPrivInvalidAuthProtocolInvalidPrivProtocolUsingSnmpVersion3() throws IOException {
        String oId = "1.6.1.2.1.1.7.0";
        String ipAddress = "localhost";
        int port = 123;
        int snmpVersion = 3; // version 3

        final PDU pdu = mock(PDU.class);
        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress(ipAddress);
        device.setDeviceType(asGId("15257"));
        device.setPort(port);
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        device.setSecurityLevel(3);
        device.setAuthProtocol(1);
        device.setAuthProtocolPassword("authpass");
        device.setPrivacyProtocol(0);
        Vector variableBindings = mock(Vector.class);
        VariableBinding variableBinding = mock(VariableBinding.class);
        Variable variable = mock(Variable.class);
        PduListener pduListener = mock(PduListener.class);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onPduReceived(pdu);
    }

    @Test
    public void ShouldReturnNullResponseWithAuthNoPrivInvalidAuthProtocolInvalidPrivProtocolPwdUsingSnmpVersion3() throws IOException {
        String oId = "1.6.1.2.1.1.7.0";
        String ipAddress = "localhost";
        int port = 123;
        int snmpVersion = 3; // version 3

        final PDU pdu = mock(PDU.class);
        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress(ipAddress);
        device.setDeviceType(asGId("15257"));
        device.setPort(port);
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        device.setSecurityLevel(3);
        device.setAuthProtocol(1);
        device.setAuthProtocolPassword("authpass");
        device.setPrivacyProtocol(1);
        device.setPrivacyProtocolPassword("privpwd");
        Vector variableBindings = mock(Vector.class);
        VariableBinding variableBinding = mock(VariableBinding.class);
        Variable variable = mock(Variable.class);
        PduListener pduListener = mock(PduListener.class);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onPduReceived(pdu);
    }

    @Test
    public void ShouldReturnNullResponseWithAuthNoPrivInvalidAuthProtocolInvalidPollingPortUsingSnmpVersion3() throws IOException {
        String oId = "1.6.1.2.1.1.7.0";
        String ipAddress = "localhost";
        int port = 123;
        int snmpVersion = 3; // version 3

        final PDU pdu = mock(PDU.class);
        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress(ipAddress);
        device.setDeviceType(asGId("15257"));
        device.setPort(port);
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        device.setSecurityLevel(3);
        device.setAuthProtocol(1);
        device.setAuthProtocolPassword("authpass");
        device.setPrivacyProtocol(2);
        device.setPrivacyProtocolPassword("privpass");
        Vector variableBindings = mock(Vector.class);
        VariableBinding variableBinding = mock(VariableBinding.class);
        Variable variable = mock(Variable.class);
        PduListener pduListener = mock(PduListener.class);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onPduReceived(pdu);
    }
}
