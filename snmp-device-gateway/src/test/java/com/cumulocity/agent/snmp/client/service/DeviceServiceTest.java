package com.cumulocity.agent.snmp.client.service;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.snmp4j.TransportMapping;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OctetString;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.test.util.ReflectionTestUtils;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.agent.snmp.utils.Constants;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

@RunWith(MockitoJUnitRunner.class)
public class DeviceServiceTest {

	@Mock
	TrapHandler trapHandler;

	@Mock
	GatewayDataProvider gatewayDataProvider;

	@Mock
	GatewayProperties.SnmpProperties snmpProperties;

	@InjectMocks
	DeviceService deviceService;

	private Logger logger;

	private Integer32 modeID;

	private OctetString engineID;

	private OctetString userName;

	private OctetString authPass;

	private OctetString privPass;

	private ListAppender<ILoggingEvent> listAppender;

	private ManagedObjectRepresentation snmpDeviceMo;

	private Map<String, Object> deviceAuthMap = new HashMap<>();

	private Map<String, Object> SnmpDeviceProperties = new HashMap<>();

	private Map<String, DeviceManagedObjectWrapper> deviceProtocolMap = new HashMap<>();

	@Before
	public void setup() {
		listAppender = new ListAppender<>();
		logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		logger.addAppender(listAppender);
		listAppender.start();

		modeID = new Integer32(SecurityModel.SECURITY_MODEL_USM);

		engineID = new OctetString("45:U9:39:900:FJ8");
		userName = new OctetString("user1");
		authPass = new OctetString("AuthPassw0rd");
		privPass = new OctetString("Passw0rd");

		deviceAuthMap.put("engineId", engineID.toString());
		deviceAuthMap.put("username", userName.toString());
		deviceAuthMap.put("authProtocol", 1);
		deviceAuthMap.put("authPassword", authPass.toString());
		deviceAuthMap.put("privProtocol", 1);
		deviceAuthMap.put("privPassword", privPass.toString());
		deviceAuthMap.put("securityLevel", 3);

		SnmpDeviceProperties.put("ipAddress", "127.0.0.1");
		SnmpDeviceProperties.put("port", "161");
		SnmpDeviceProperties.put("type", "/inventory/managedObjects/3451");
		SnmpDeviceProperties.put("version", 3);
		SnmpDeviceProperties.put("auth", deviceAuthMap);

		snmpDeviceMo = new ManagedObjectRepresentation();
		snmpDeviceMo.setId(new GId("snmp-device"));
		snmpDeviceMo.set(SnmpDeviceProperties, Constants.C8Y_SNMP_DEVICE);

		DeviceManagedObjectWrapper deviceMoWrapper = new DeviceManagedObjectWrapper(snmpDeviceMo);
		deviceProtocolMap.put("snmp-device", deviceMoWrapper);

		when(snmpProperties.getTrapListenerThreadPoolSize()).thenReturn(1);
		when(snmpProperties.getTrapListenerAddress()).thenReturn("127.0.0.1");
		when(snmpProperties.getTrapListenerPort()).thenReturn(161);
		when(gatewayDataProvider.getDeviceProtocolMap()).thenReturn(Collections.emptyMap());
	}

	@Test
	public void shouldConfigureUSMOnBootstrapReadyEvent() {
		when(snmpProperties.getTrapListenerProtocol()).thenReturn("udp");

		SecurityModel securityModel = SecurityModels.getInstance().getSecurityModel(modeID);
		assertNull(securityModel);

		// Action
		ReflectionTestUtils.invokeMethod(deviceService, "createSnmpDeviceListener");

		securityModel = SecurityModels.getInstance().getSecurityModel(modeID);
		assertNotNull(securityModel);
		assertThat(securityModel, instanceOf(USM.class));
	}

	@Test
	public void shouldAddUserToUSMForTheDevicesWithSnmpV3Version() {
		when(snmpProperties.getTrapListenerProtocol()).thenReturn("udp");
		when(gatewayDataProvider.getDeviceProtocolMap()).thenReturn(deviceProtocolMap);

		SecurityModel securityModel = SecurityModels.getInstance().getSecurityModel(modeID);
		assertNull(securityModel);

		// Action
		ReflectionTestUtils.invokeMethod(deviceService, "createSnmpDeviceListener");

		securityModel = SecurityModels.getInstance().getSecurityModel(modeID);
		assertNotNull(securityModel);
		assertThat(securityModel, instanceOf(USM.class));
		assertEquals(1, ((USM) securityModel).getUserTable().getUserEntries().size());

		UsmUser user = ((USM) securityModel).getUserTable().getUser(engineID, userName).getUsmUser();
		assertEquals(userName, user.getSecurityName());
		assertEquals(AuthMD5.ID, user.getAuthenticationProtocol());
		assertEquals(authPass, user.getAuthenticationPassphrase());
		assertEquals(PrivDES.ID, user.getPrivacyProtocol());
		assertEquals(privPass, user.getPrivacyPassphrase());
	}

	@Test
	public void shouldUpdateUSMOnGatewayDataRefreshedEvent() {
		// Add valid version 3 user credentials
		shouldAddUserToUSMForTheDevicesWithSnmpV3Version();

		// Changing SNMP version to 1 for the same device
		SnmpDeviceProperties.put("version", 1);
		DeviceManagedObjectWrapper deviceMoWrapper = new DeviceManagedObjectWrapper(snmpDeviceMo);
		gatewayDataProvider.getDeviceProtocolMap().put("snmp-device", deviceMoWrapper);

		// Action
		ReflectionTestUtils.invokeMethod(deviceService, "refreshCredentials");

		SecurityModel securityModel = SecurityModels.getInstance().getSecurityModel(modeID);
		assertNotNull(securityModel);
		assertThat(securityModel, instanceOf(USM.class));
		assertEquals(0, ((USM) securityModel).getUserTable().getUserEntries().size());
	}

	@Test
	public void shouldNotAddUserIfSecurityLevelIsInvalid() {
		int invalidSecurityLevel = -1;
		String errorMsg = "Unsupported " + invalidSecurityLevel + " Security level found in " + userName.toString()
				+ " user configured for device having " + engineID.toString() + " as engine id";

		deviceAuthMap.put("securityLevel", invalidSecurityLevel);
		DeviceManagedObjectWrapper deviceMoWrapper = new DeviceManagedObjectWrapper(snmpDeviceMo);
		deviceProtocolMap.put("snmp-device", deviceMoWrapper);

		when(gatewayDataProvider.getDeviceProtocolMap()).thenReturn(deviceProtocolMap);

		SecurityModel securityModel = SecurityModels.getInstance().getSecurityModel(modeID);
		assertNull(securityModel);

		// Action
		ReflectionTestUtils.invokeMethod(deviceService, "configureUserSecurityModel");

		securityModel = SecurityModels.getInstance().getSecurityModel(modeID);
		assertNotNull(securityModel);
		assertThat(securityModel, instanceOf(USM.class));
		assertEquals(0, ((USM) securityModel).getUserTable().getUserEntries().size());
		assertTrue(checkLogExist(errorMsg));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldConfigureTcpTransportMappingForTcp() {
		when(snmpProperties.getTrapListenerProtocol()).thenReturn("tcp");

		assertNull(deviceService.snmp);

		// Action
		ReflectionTestUtils.invokeMethod(deviceService, "createSnmpDeviceListener");

		assertNotNull(deviceService.snmp);

		Collection<TransportMapping> mappings = deviceService.snmp.getMessageDispatcher().getTransportMappings();
		assertNotNull(mappings);
		assertEquals(1, mappings.size());

		mappings.forEach(transportMapping -> {
			assertThat(transportMapping, instanceOf(DefaultTcpTransportMapping.class));
		});
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldConfigureUdpTransportMappingForUdp() {
		when(snmpProperties.getTrapListenerProtocol()).thenReturn("udp");

		assertNull(deviceService.snmp);

		// Action
		ReflectionTestUtils.invokeMethod(deviceService, "createSnmpDeviceListener");

		assertNotNull(deviceService.snmp);

		Collection<TransportMapping> mappings = deviceService.snmp.getMessageDispatcher().getTransportMappings();
		assertNotNull(mappings);
		assertEquals(1, mappings.size());

		mappings.forEach(transportMapping -> {
			assertThat(transportMapping, instanceOf(DefaultUdpTransportMapping.class));
		});
	}

	@Test
	public void shouldLogErrorForInvalidTransportProtocol() {
		String errorMsg = "Failed while staring the trap listener";

		when(snmpProperties.getTrapListenerProtocol()).thenReturn("ip");

		// Action
		ReflectionTestUtils.invokeMethod(deviceService, "createSnmpDeviceListener");

		assertNull(deviceService.snmp);
		assertTrue(checkLogExist(errorMsg));
	}

	@After
	public void tearDown() {
		deviceService.stop();

		SecurityModels.getInstance().removeSecurityModel(modeID);

		listAppender.stop();
		listAppender.list.clear();
		logger.detachAppender(listAppender);
	}

	private boolean checkLogExist(String errorMsg) {
		AtomicBoolean found = new AtomicBoolean(false);
		listAppender.list.forEach(logEvent -> {
			if (logEvent.getFormattedMessage().equalsIgnoreCase(errorMsg)) {
				found.set(true);
				assertEquals(Level.ERROR, logEvent.getLevel());
			}
		});

		return found.get();
	}
}
