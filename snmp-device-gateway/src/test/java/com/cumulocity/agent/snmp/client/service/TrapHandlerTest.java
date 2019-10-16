package com.cumulocity.agent.snmp.client.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;

import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.DeviceProtocolManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.GatewayManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.pubsub.publisher.AlarmPublisher;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

@RunWith(MockitoJUnitRunner.class)
public class TrapHandlerTest {

	@Mock
	AlarmPublisher alarmPublisher;

	@Mock
	GatewayDataProvider gatewayDataProvider;

	@InjectMocks
	TrapHandler trapHandler;

	private Logger logger;

	private ListAppender<ILoggingEvent> listAppender;

	@Before
	public void setup() {
		listAppender = new ListAppender<>();
		logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		logger.addAppender(listAppender);
		listAppender.start();
	}

	@Test
	public void shouldLogErrorMessageIfPDUIsNotPresentInTheTrap() {
		CommandResponderEvent event = mock(CommandResponderEvent.class);
		String errorMsg = "No data present in the received trap";

		when(event.getPDU()).thenReturn(null);

		// Action
		trapHandler.processPdu(event);

		assertTrue(checkLogExist(Level.ERROR, errorMsg));
	}

	@Test
	public void shouldNotProceedProcessingTrapIfPeerAddressIsInvalid() {
		PDU pdu = mock(PDU.class);
		CommandResponderEvent event = mock(CommandResponderEvent.class);
		String errorMsg = "Failed to translate peer address " + event;

		when(event.getPDU()).thenReturn(pdu);
		when(event.getPeerAddress()).thenReturn(null);

		// Action
		trapHandler.processPdu(event);

		assertTrue(checkLogExist(Level.ERROR, errorMsg));
	}

	@Test
	public void shouldNotProcessTrapFromAnUnknownDevice() {
		PDU pdu = mock(PDU.class);
		CommandResponderEvent event = mock(CommandResponderEvent.class);
		Address address = new UdpAddress("10.0.0.1/65214");
		ManagedObjectRepresentation gatewayDeviceMo = new ManagedObjectRepresentation();
		gatewayDeviceMo.setId(new GId("snmp-agent"));
		GatewayManagedObjectWrapper gatewayWrapper = new GatewayManagedObjectWrapper(gatewayDeviceMo);
		String errorMsg = "Trap received from an unknown device with '10.0.0.1' IP address";

		when(event.getPDU()).thenReturn(pdu);
		when(event.getPeerAddress()).thenReturn(address);
		when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayWrapper);
		when(gatewayDataProvider.getDeviceProtocolMap()).thenReturn(Collections.emptyMap());

		// Action
		trapHandler.processPdu(event);

		assertTrue(checkLogExist(Level.ERROR, errorMsg));
	}

	@Test
	public void shouldRaiseAlarmForTrapFromAnUnknownDevice() {
		PDU pdu = mock(PDU.class);
		CommandResponderEvent event = mock(CommandResponderEvent.class);
		Address address = new UdpAddress("10.0.0.1/65214");
		ManagedObjectRepresentation gatewayDeviceMo = new ManagedObjectRepresentation();
		gatewayDeviceMo.setId(new GId("snmp-agent"));
		GatewayManagedObjectWrapper gatewayWrapper = new GatewayManagedObjectWrapper(gatewayDeviceMo);
		ArgumentCaptor<AlarmRepresentation> captor = ArgumentCaptor.forClass(AlarmRepresentation.class);

		when(event.getPDU()).thenReturn(pdu);
		when(event.getPeerAddress()).thenReturn(address);
		when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayWrapper);
		when(gatewayDataProvider.getDeviceProtocolMap()).thenReturn(Collections.emptyMap());

		// Action
		trapHandler.processPdu(event);
		verify(alarmPublisher).publish(captor.capture());

		AlarmRepresentation alarm = captor.getValue();
		assertEquals("MAJOR", alarm.getSeverity());
		assertEquals("c8y_TRAPReceivedFromUnknownDevice", alarm.getType());
		assertEquals(gatewayDeviceMo, alarm.getSource());
	}

	@Test
	public void shouldNotProceedProcessingTrapIfVariableBindingIsMissing() {
		logger.setLevel(Level.DEBUG);

		PDU pdu = mock(PDU.class);
		CommandResponderEvent event = mock(CommandResponderEvent.class);
		Address address = new UdpAddress("10.0.0.1/65214");
		Map<String, DeviceManagedObjectWrapper> deviceMap = new HashMap<>();
		deviceMap.put("10.0.0.1", null);
		String errorMsg = "No OID found in the received trap";

		when(event.getPDU()).thenReturn(pdu);
		when(event.getPeerAddress()).thenReturn(address);
		when(gatewayDataProvider.getDeviceProtocolMap()).thenReturn(deviceMap);
		when(pdu.getVariableBindings()).thenReturn(null);

		// Action
		trapHandler.processPdu(event);

		assertTrue(checkLogExist(Level.DEBUG, errorMsg));
	}

	@Test
	public void shouldNotProceedProcessingTrapIfProtocolObjectIsMissing() {
		PDU pdu = mock(PDU.class);
		CommandResponderEvent event = mock(CommandResponderEvent.class);
		VariableBinding variableBinding = mock(VariableBinding.class);
		DeviceManagedObjectWrapper deviceMoWrapper = mock(DeviceManagedObjectWrapper.class);

		Map<String, DeviceProtocolManagedObjectWrapper> protocolMap = new HashMap<>();
		protocolMap.put("device-protocol", null);

		Map<String, DeviceManagedObjectWrapper> deviceMap = new HashMap<>();
		deviceMap.put("10.0.0.1", deviceMoWrapper);

		Vector<VariableBinding> variableBindings = new Vector<>();
		variableBindings.add(variableBinding);

		Address address = new UdpAddress("10.0.0.1/65214");
		String errorMsg = "device-protocol device procotol object not found at the gateway for the 10.0.0.1 device";

		when(event.getPeerAddress()).thenReturn(address);
		when(event.getPDU()).thenReturn(pdu);
		Mockito.doReturn(variableBindings).when(pdu).getVariableBindings();
		when(deviceMoWrapper.getDeviceProtocol()).thenReturn("device-protocol");
		when(gatewayDataProvider.getDeviceProtocolMap()).thenReturn(deviceMap);
		when(gatewayDataProvider.getProtocolMap()).thenReturn(protocolMap);

		// Action
		trapHandler.processPdu(event);

		assertTrue(checkLogExist(Level.ERROR, errorMsg));
	}

	@After
	public void tearDown() {
		listAppender.stop();
		listAppender.list.clear();
		logger.detachAppender(listAppender);
	}

	private boolean checkLogExist(Level logLevel, String errorMsg) {
		AtomicBoolean found = new AtomicBoolean(false);
		listAppender.list.forEach(logEvent -> {
			if (logEvent.getFormattedMessage().equalsIgnoreCase(errorMsg)) {
				found.set(true);
				assertEquals(logLevel, logEvent.getLevel());
			}
		});

		return found.get();
	}
}
