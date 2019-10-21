package com.cumulocity.agent.snmp.device.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.GatewayManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.snmp4j.TransportMapping;
import org.snmp4j.security.*;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OctetString;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.security.Permission;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeviceListenerServiceTest {

	@Mock
	TrapHandler trapHandler;

	@Mock
	GatewayDataProvider gatewayDataProvider;

	@Mock
	GatewayProperties.SnmpProperties snmpProperties;

	@Mock
	TaskScheduler taskScheduler;

	@Mock
	GatewayManagedObjectWrapper gatewayDeviceWrapper;

	@Mock
	GatewayManagedObjectWrapper.SnmpCommunicationProperties snmpCommunicationProperties;
	
	@Mock
	ScheduledFuture<?> snmpDevicePoller;

	@Spy
	@InjectMocks
	DeviceListenerService deviceListenerService;

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
		logger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
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
		snmpDeviceMo.set(SnmpDeviceProperties, DeviceManagedObjectWrapper.C8Y_SNMP_DEVICE);

		DeviceManagedObjectWrapper deviceMoWrapper = new DeviceManagedObjectWrapper(snmpDeviceMo);
		deviceProtocolMap.put("snmp-device", deviceMoWrapper);

		when(snmpProperties.getTrapListenerThreadPoolSize()).thenReturn(1);
		when(snmpProperties.getTrapListenerAddress()).thenReturn("127.0.0.1");
		when(snmpProperties.getTrapListenerPort()).thenReturn(161);
		when(snmpProperties.getTrapListenerProtocol()).thenReturn("UDP");
		when(gatewayDataProvider.getDeviceProtocolMap()).thenReturn(Collections.emptyMap());
		when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayDeviceWrapper);
		when(gatewayDeviceWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationProperties);
		when(snmpCommunicationProperties.getPollingRateInMinutes()).thenReturn(1L);

		System.setSecurityManager(new NoExitSecurityManager());
	}

	@Test
	public void shouldConfigureUSMOnBootstrapReadyEvent() {
		when(snmpProperties.getTrapListenerProtocol()).thenReturn("udp");

		SecurityModel securityModel = SecurityModels.getInstance().getSecurityModel(modeID);
		assertNull(securityModel);

		// Action
		ReflectionTestUtils.invokeMethod(deviceListenerService, "createSnmpDeviceListener");

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
		ReflectionTestUtils.invokeMethod(deviceListenerService, "createSnmpDeviceListener");

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

	@Test(expected = ExitException.class)
	public void shouldCreateSnmpDeviceListenerFailIfPortIsAlreadyInUse() {
		when(snmpProperties.getTrapListenerThreadPoolSize()).thenReturn(1);
		when(snmpProperties.getTrapListenerProtocol()).thenReturn("TCP");
		when(snmpProperties.getTrapListenerAddress()).thenReturn("127.0.0.1");
		when(snmpProperties.getTrapListenerPort()).thenReturn(162);

		// Action
		ReflectionTestUtils.invokeMethod(deviceListenerService, "createSnmpDeviceListener");

		// Trying to listen on same port again
		try {
			ReflectionTestUtils.invokeMethod(deviceListenerService, "createSnmpDeviceListener");
		} catch(ExitException ee) {
			checkLogExist("Failed to start listening to traps. Port 127.0.0.1/162 is already in use.");
			checkLogExist("Update the 'snmp.trapListener.port' and 'snmp.trapListener.address' properties and restart the agent. Shutting down the agent...");

			throw ee;
		}
	}

	@Test(expected = ExitException.class)
	public void shouldCreateSnmpDeviceListenerFailIfBindingAddressIsInvalid() {
		when(snmpProperties.getTrapListenerThreadPoolSize()).thenReturn(1);
		when(snmpProperties.getTrapListenerAddress()).thenReturn("localhost");

		// Action
		try {
			ReflectionTestUtils.invokeMethod(deviceListenerService, "createSnmpDeviceListener");
		} catch(ExitException ee) {
			checkLogExist("Failed to start listening to traps on port 127.0.0.1/162.");
			checkLogExist("Update the 'snmp.trapListener.port' and 'snmp.trapListener.address' properties and restart the agent. Shutting down the agent...");

			throw ee;
		}
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
		ReflectionTestUtils.invokeMethod(deviceListenerService, "onGatewayDataRefresh");

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
		ReflectionTestUtils.invokeMethod(deviceListenerService, "configureUserSecurityModel");

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

		assertNull(deviceListenerService.snmp);

		// Action
		ReflectionTestUtils.invokeMethod(deviceListenerService, "createSnmpDeviceListener");

		assertNotNull(deviceListenerService.snmp);

		Collection<TransportMapping> mappings = deviceListenerService.snmp.getMessageDispatcher()
				.getTransportMappings();
		assertNotNull(mappings);
		assertEquals(1, mappings.size());

		mappings.forEach((TransportMapping transportMapping) -> {
			assertThat(transportMapping, instanceOf(DefaultTcpTransportMapping.class));
		});
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldConfigureUdpTransportMappingForUdp() {
		when(snmpProperties.getTrapListenerProtocol()).thenReturn("udp");

		assertNull(deviceListenerService.snmp);

		// Action
		ReflectionTestUtils.invokeMethod(deviceListenerService, "createSnmpDeviceListener");

		assertNotNull(deviceListenerService.snmp);

		Collection<TransportMapping> mappings = deviceListenerService.snmp.getMessageDispatcher()
				.getTransportMappings();
		assertNotNull(mappings);
		assertEquals(1, mappings.size());

		mappings.forEach(transportMapping -> {
			assertThat(transportMapping, instanceOf(DefaultUdpTransportMapping.class));
		});
	}

	@Test(expected = IOException.class)
	public void shouldLogErrorForInvalidTransportProtocol() throws IOException {
		String errorMsg = "Unable to service snmp devices. Unsupported";

		// Action
		try {
			deviceListenerService.createTransportMapping("", 1010, "localhost");
		} catch (IOException e) {
			assertNull(deviceListenerService.snmp);
			assertTrue(checkLogExist(errorMsg));

			throw e;
		}
	}

	@Test
	public void shouldStartTrapListeningWhileHandlingBootstrapReadyEvent() {
		// Action
		ReflectionTestUtils.invokeMethod(deviceListenerService, "onBootstrapReady");

		verify(deviceListenerService, times(1)).createSnmpDeviceListener();
	}

	@Test
	public void shouldInitiateDevicePollingWhileHandlingBootstrapReadyEvent() {
		// Action
		ReflectionTestUtils.invokeMethod(deviceListenerService, "onBootstrapReady");

		verify(deviceListenerService, times(1)).createSnmpDevicePoller();
	}

	@Test
	public void shouldReconfigurePollingScheduledJobIfIntervalChanges() {
		ReflectionTestUtils.setField(deviceListenerService, "pollingRateInMinutes", 1L);
		when(snmpCommunicationProperties.getPollingRateInMinutes()).thenReturn(123L);

		// Action
		ReflectionTestUtils.invokeMethod(deviceListenerService, "onGatewayDataRefresh");

		verify(deviceListenerService, times(1)).createSnmpDevicePoller();
		verify(taskScheduler).scheduleWithFixedDelay(any(Runnable.class), eq(Duration.ofMinutes(123L)));
	}

	@Test
	public void shouldNotReconfigurePollingScheduledJobIfIntervalIsSame() {
		ReflectionTestUtils.setField(deviceListenerService, "snmpDevicePoller", snmpDevicePoller);
		ReflectionTestUtils.setField(deviceListenerService, "pollingRateInMinutes", 1L);
		when(snmpCommunicationProperties.getPollingRateInMinutes()).thenReturn(1L);
		when(snmpDevicePoller.isDone()).thenReturn(false);

		// Action
		ReflectionTestUtils.invokeMethod(deviceListenerService, "onGatewayDataRefresh");

		verify(deviceListenerService, times(1)).createSnmpDevicePoller();
		verify(taskScheduler, times(0)).scheduleWithFixedDelay(any(Runnable.class), eq(Duration.ofMinutes(1L)));
	}

	@After
	public void tearDown() {
		ReflectionTestUtils.invokeMethod(deviceListenerService, "stop");

		SecurityModels.getInstance().removeSecurityModel(modeID);

		listAppender.stop();
		listAppender.list.clear();
		logger.detachAppender(listAppender);

		System.setSecurityManager(null); // or save and restore original
	}

	private boolean checkLogExist(String errorMsg) {
		AtomicBoolean found = new AtomicBoolean(false);
		listAppender.list.forEach(logEvent -> {
			if (logEvent.getFormattedMessage().contains(errorMsg)) {
				found.set(true);
				assertEquals(Level.ERROR, logEvent.getLevel());
			}
		});

		return found.get();
	}

	private static class ExitException extends SecurityException {
		final int status;

		ExitException(int status) {
			super("There is no escape!");
			this.status = status;
		}
	}

	private static class NoExitSecurityManager extends SecurityManager {
		@Override
		public void checkPermission(Permission perm) {
			// allow anything.
		}

		@Override
		public void checkPermission(Permission perm, Object context) {
			// allow anything.
		}

		@Override
		public void checkExit(int status) {
			super.checkExit(status);
			throw new ExitException(status);
		}
	}
}
