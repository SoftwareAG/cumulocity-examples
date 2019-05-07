package com.cumulocity.snmp.service.client;

import com.cumulocity.snmp.configuration.service.SNMPConfigurationProperties;
import com.cumulocity.snmp.model.gateway.device.Device;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.snmp4j.PDU;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.springframework.context.ApplicationEventPublisher;

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

    String oId;
    private Device device;
    private Vector variableBindings;
    private VariableBinding variableBinding;
    private Variable variable;
    private PduListener pduListener;
    private PDU pdu;

    @Before
    public void init() {
        oId = "1.3.6.1.2.1.1.7.0";
        String ipAddress = "localhost";
        int port = 123;

        pdu = mock(PDU.class);

        device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress(ipAddress);
        device.setDeviceType(asGId("15257"));
        device.setPort(port);
        variableBindings = mock(Vector.class);
        variableBinding = mock(VariableBinding.class);
        variable = mock(Variable.class);
        pduListener = mock(PduListener.class);
    }

    @Test
    public void ShouldReturnResponseAsNullForPollingIncorrectDevicePortUsingSnmpVersion1() {
        int snmpVersion = 0; // version 1
        device.setSnmpVersion(snmpVersion);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");
        doNothing().when(pduListener).onVariableBindingReceived(any(VariableBinding.class));

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onVariableBindingReceived(any(VariableBinding.class));
    }

    @Test
    public void ShouldReturnResponseAsNullForPollingIncorrectOidUsingSnmpVersion1() {
        int snmpVersion = 0; // version 1
        device.setSnmpVersion(snmpVersion);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onVariableBindingReceived(variableBinding);
    }

    @Test
    public void ShouldReturnResponseAsNullForPollingIncorrectDevicePortUsingSnmpVersion2c() {
        int snmpVersion = 1; // version 2c
        device.setSnmpVersion(snmpVersion);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");
        doNothing().when(pduListener).onVariableBindingReceived(any(VariableBinding.class));

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onVariableBindingReceived(any(VariableBinding.class));
    }

    @Test
    public void ShouldReturnResponseAsNullForPollingIncorrectOidUsingSnmpVersion2c() {
        int snmpVersion = 1; // version 2c
        device.setSnmpVersion(snmpVersion);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onVariableBindingReceived(variableBinding);
    }

    @Test
    public void ShouldReturnNullResponseAsNoUsernameProvidedUsingSnmpVersion3() {
        int snmpVersion = 3; // version 3
        device.setSnmpVersion(snmpVersion);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onVariableBindingReceived(variableBinding);
    }

    @Test
    public void ShouldReturnNullResponseAsNoSecurityLevelProvidedUsingSnmpVersion3() {
        int snmpVersion = 3; // version 3
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onVariableBindingReceived(variableBinding);
    }

    @Test
    public void ShouldReturnNullResponseAsUndefinedSecurityLevelProvidedUsingSnmpVersion3() {
        int snmpVersion = 3; // version 3
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        device.setSecurityLevel(0);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onVariableBindingReceived(variableBinding);
    }

    @Test
    public void ShouldReturnNullResponseWithNoAuthNoPrivUsingSnmpVersion3() {
        int snmpVersion = 3; // version 3
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        device.setSecurityLevel(1);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onVariableBindingReceived(variableBinding);
    }

    @Test
    public void ShouldReturnNullResponseWithAuthNoPrivInvalidAuthProtocolUsingSnmpVersion3() {
        int snmpVersion = 3; // version 3
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        device.setSecurityLevel(2);
        device.setAuthProtocol(0);
        device.setAuthProtocolPassword("authpassword");

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onVariableBindingReceived(variableBinding);
    }

    @Test
    public void ShouldReturnNullResponseWithAuthNoPrivInvalidAuthProtocolInvalidPasswordUsingSnmpVersion3() {
        int snmpVersion = 3; // version 3
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        device.setSecurityLevel(2);
        device.setAuthProtocol(1);
        device.setAuthProtocolPassword("auth123");

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onVariableBindingReceived(variableBinding);
    }

    @Test
    public void ShouldReturnNullResponseWithAuthNoPrivInvalidAuthProtocolInvalidPortNumberUsingSnmpVersion3() {
        int snmpVersion = 3; // version 3
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        device.setSecurityLevel(2);
        device.setAuthProtocol(2);
        device.setAuthProtocolPassword("authpass");

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onVariableBindingReceived(variableBinding);
    }

    @Test
    public void ShouldReturnNullResponseWithAuthNoPrivInvalidAuthProtocolInvalidPrivProtocolUsingSnmpVersion3() {
        int snmpVersion = 3; // version 3
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        device.setSecurityLevel(3);
        device.setAuthProtocol(1);
        device.setAuthProtocolPassword("authpass");
        device.setPrivacyProtocol(0);

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onVariableBindingReceived(variableBinding);
    }

    @Test
    public void ShouldReturnNullResponseWithAuthNoPrivInvalidAuthProtocolInvalidPrivProtocolPwdUsingSnmpVersion3() {
        int snmpVersion = 3; // version 3
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        device.setSecurityLevel(3);
        device.setAuthProtocol(1);
        device.setAuthProtocolPassword("authpass");
        device.setPrivacyProtocol(1);
        device.setPrivacyProtocolPassword("privpwd");

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onVariableBindingReceived(variableBinding);
    }

    @Test
    public void ShouldReturnNullResponseWithAuthNoPrivInvalidAuthProtocolInvalidPollingPortUsingSnmpVersion3() {
        int snmpVersion = 3; // version 3
        device.setSnmpVersion(snmpVersion);
        device.setUsername("testuser");
        device.setSecurityLevel(3);
        device.setAuthProtocol(1);
        device.setAuthProtocolPassword("authpass");
        device.setPrivacyProtocol(2);
        device.setPrivacyProtocolPassword("privpass");

        when(pdu.getVariableBindings()).thenReturn(variableBindings);
        when(variableBindings.get(0)).thenReturn(variableBinding);
        when(variableBinding.getVariable()).thenReturn(variable);
        when(config.getCommunityTarget()).thenReturn("public");

        pollingService.initiatePolling(oId, device, pduListener);

        verify(pduListener, never()).onVariableBindingReceived(variableBinding);
    }
}
