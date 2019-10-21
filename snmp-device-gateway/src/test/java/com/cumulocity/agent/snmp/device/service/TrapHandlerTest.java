package com.cumulocity.agent.snmp.device.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.cumulocity.agent.snmp.platform.model.*;
import com.cumulocity.agent.snmp.platform.pubsub.publisher.AlarmPublisher;
import com.cumulocity.agent.snmp.platform.pubsub.publisher.EventPublisher;
import com.cumulocity.agent.snmp.platform.pubsub.publisher.MeasurementPublisher;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
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
import org.snmp4j.smi.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TrapHandlerTest {
	@Mock
	private PDU pdu;

	@Mock
	private CommandResponderEvent event;

	@Mock
	private AlarmPublisher alarmPublisher;

	@Mock
	private EventPublisher eventPublisher;

	@Mock
	private MeasurementPublisher measurementPublisher;

	@Mock
	private GatewayDataProvider gatewayDataProvider;

	@Mock
	private DeviceManagedObjectWrapper device1MoWrapper;

	@Mock
	private DeviceManagedObjectWrapper device2MoWrapper;
	
	@Mock
	private DeviceProtocolManagedObjectWrapper protocolMoWrapper;
	
	@InjectMocks
	private TrapHandler trapHandler;

	private Logger logger;

	private ListAppender<ILoggingEvent> listAppender;

	private ArgumentCaptor<AlarmRepresentation> alarmCaptor;

	private ArgumentCaptor<EventRepresentation> eventCaptor;

	private ArgumentCaptor<MeasurementRepresentation> measurementCaptor;

	private ArgumentCaptor<ManagedObjectRepresentation> gatewayDeviceMoCaptor;

	private Address address;

	private ManagedObjectRepresentation gatewayDeviceMo;

	private GatewayManagedObjectWrapper gatewayWrapper;

	private AlarmMapping alarmMapping;

	private EventMapping eventMapping;

	private MeasurementMapping measurementMapping;
	
	private ManagedObjectRepresentation device1Mo;
	
	private Register register;
	
	private Map<String, Register> oidMap;
	
	private Map<String, DeviceProtocolManagedObjectWrapper> protocolMap;
	
	private Map<String, DeviceManagedObjectWrapper> deviceMap;

	@Before
	public void setup() {
		listAppender = new ListAppender<>();
		logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		logger.addAppender(listAppender);
		logger.setLevel(Level.DEBUG);
		listAppender.start();

		alarmCaptor = ArgumentCaptor.forClass(AlarmRepresentation.class);
		eventCaptor = ArgumentCaptor.forClass(EventRepresentation.class);
		measurementCaptor = ArgumentCaptor.forClass(MeasurementRepresentation.class);
		gatewayDeviceMoCaptor = ArgumentCaptor.forClass(ManagedObjectRepresentation.class);

		address = new UdpAddress("10.0.0.1/65214");

		gatewayDeviceMo = new ManagedObjectRepresentation();
		gatewayDeviceMo.setId(new GId("snmp-agent"));
		gatewayWrapper = new GatewayManagedObjectWrapper(gatewayDeviceMo);

		alarmMapping = new AlarmMapping();
		alarmMapping.setType("c8y_SNMPAlarm");
		alarmMapping.setText("Test SNMP alarm");
		alarmMapping.setSeverity(AlarmSeverity.MINOR.name());

		eventMapping = new EventMapping();
		eventMapping.setType("c8y_SNMPEvent");
		eventMapping.setText("Test SNMP event");

		measurementMapping = new MeasurementMapping();
		measurementMapping.setType("c8y_SNMPMeasurement");
		measurementMapping.setSeries("T");
		
		device1Mo = new ManagedObjectRepresentation();
		device1Mo.setId(new GId("snmp-device1"));

		protocolMap = new HashMap<>();
		protocolMap.put("device-protocol", protocolMoWrapper);

		deviceMap = new HashMap<>();
		deviceMap.put("10.0.0.1", device1MoWrapper);
		deviceMap.put("10.0.0.2", device2MoWrapper);

		OID oid1 = new OID("1.3.6.1.4.868.2.4.1.1");
		Variable variable1 = new Integer32(10);
		VariableBinding variableBinding1 = new VariableBinding(oid1, variable1);

		Vector<VariableBinding> variableBindings = new Vector<>();
		variableBindings.add(variableBinding1);

		register = new Register();
		register.setOid("1.3.6.1.4.868.2.4.1.1");
		register.setUnit("cm");

		oidMap = new HashMap<>();
		oidMap.put("1.3.6.1.4.868.2.4.1.1", register);

		Mockito.doReturn(variableBindings).when(pdu).getVariableBindings();

		when(event.getPDU()).thenReturn(pdu);
		when(event.getPeerAddress()).thenReturn(address);
		when(protocolMoWrapper.getOidMap()).thenReturn(oidMap);
		when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayWrapper);
		when(device1MoWrapper.getDeviceProtocol()).thenReturn("device-protocol");
		when(device1MoWrapper.getManagedObject()).thenReturn(device1Mo);
		when(gatewayDataProvider.getProtocolMap()).thenReturn(protocolMap);
		when(gatewayDataProvider.getDeviceProtocolMap()).thenReturn(deviceMap);
	}

	@Test
	public void shouldLogErrorMessageIfPDUIsNotPresentInTheTrap() {
		String errorMsg = "No data present in the received trap";

		when(event.getPDU()).thenReturn(null);

		// Action
		trapHandler.processPdu(event);

		assertTrue(checkLogExist(Level.ERROR, errorMsg));
	}

	@Test
	public void shouldNotProceedProcessingTrapIfPeerAddressIsInvalid() {
		String errorMsg = "Failed to translate received trap.\n" + event;

		when(event.getPeerAddress()).thenReturn(null);

		// Action
		trapHandler.processPdu(event);

		assertTrue(checkLogExist(Level.ERROR, errorMsg));
	}

	@Test
	public void shouldNotProcessTrapFromAnUnknownDevice() {
		String errorMsg = "Trap received from an unknown device with IP address : 10.0.0.1";

		when(gatewayDataProvider.getDeviceProtocolMap()).thenReturn(Collections.emptyMap());

		// Action
		trapHandler.processPdu(event);

		assertTrue(checkLogExist(Level.ERROR, errorMsg));
	}

	@Test
	public void shouldRaiseAlarmForTrapFromAnUnknownDevice() {
		when(gatewayDataProvider.getDeviceProtocolMap()).thenReturn(Collections.emptyMap());

		// Action
		ManagedObjectRepresentation source = new ManagedObjectRepresentation();
		source.setId(GId.asGId(1));

		trapHandler.processPdu(event);
		verify(alarmPublisher).publish(alarmCaptor.capture());

		AlarmRepresentation alarm = alarmCaptor.getValue();
		assertEquals("MAJOR", alarm.getSeverity());
		assertEquals(AlarmMapping.c8y_TRAPReceivedFromUnknownDevice, alarm.getType());
		assertEquals(gatewayDeviceMo.getId(), alarm.getSource().getId());
	}

	@Test
	public void shouldNotProceedProcessingTrapIfVariableBindingIsMissing() {
		String errorMsg = "No OID found in the received trap";

		when(pdu.getVariableBindings()).thenReturn(null);

		// Action
		trapHandler.processPdu(event);

		assertTrue(checkLogExist(Level.DEBUG, errorMsg));
	}

	@Test
	public void shouldNotProceedProcessingTrapIfProtocolObjectIsMissing() {
		String errorMsg = "device-protocol device procotol object not found at the gateway for the device with IP address 10.0.0.1";
		
		protocolMap.put("device-protocol", null);

		// Action
		trapHandler.processPdu(event);

		assertTrue(checkLogExist(Level.ERROR, errorMsg));
	}

	@Test
	public void shouldPublishAlarmForValidTrapAndOidAlarmMapping() {
		register.setAlarmMapping(alarmMapping);

		// Action
		trapHandler.processPdu(event);

		verify(alarmPublisher, times(1)).publish(alarmCaptor.capture());
		verify(eventPublisher, times(0)).publish(eventCaptor.capture());
		verify(measurementPublisher, times(0)).publish(measurementCaptor.capture());

		AlarmRepresentation alarm = alarmCaptor.getValue();
		assertEquals("MINOR", alarm.getSeverity());
		assertEquals("c8y_SNMPAlarm", alarm.getType());
		assertEquals("Test SNMP alarm", alarm.getText());
		assertEquals(device1Mo.getId(), alarm.getSource().getId());
	}

	@Test
	public void shouldPublishEventForValidTrapAndOidEventMapping() {
		register.setEventMapping(eventMapping);

		// Action
		trapHandler.processPdu(event);

		verify(alarmPublisher, times(0)).publish(alarmCaptor.capture());
		verify(eventPublisher, times(1)).publish(eventCaptor.capture());
		verify(measurementPublisher, times(0)).publish(measurementCaptor.capture());

		EventRepresentation eventResult = eventCaptor.getValue();
		assertEquals("c8y_SNMPEvent", eventResult.getType());
		assertEquals("Test SNMP event", eventResult.getText());
		assertEquals(device1Mo.getId(), eventResult.getSource().getId());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldPublishMeasurementForValidTrapAndOidMeasurementMapping() {
		register.setMeasurementMapping(measurementMapping);

		// Action
		trapHandler.processPdu(event);

		verify(alarmPublisher, times(0)).publish(alarmCaptor.capture());
		verify(eventPublisher, times(0)).publish(eventCaptor.capture());
		verify(measurementPublisher, times(1)).publish(measurementCaptor.capture());

		MeasurementRepresentation measurement = measurementCaptor.getValue();
		assertEquals("c8y_SNMPMeasurement", measurement.getType());
		assertEquals(device1Mo.getId(), measurement.getSource().getId());

		Map<String, Object> attrs = measurement.getAttrs();
		assertTrue(attrs.containsKey(measurementMapping.getType()));
		Map<String, Map<String, Object>> typeMap = (Map<String, Map<String, Object>>)attrs.get(measurementMapping.getType());
		assertTrue(typeMap.containsKey(measurementMapping.getSeries()));

		Map<String, Object> seriesMap = typeMap.get(measurementMapping.getSeries());
		assertEquals(10, seriesMap.get("value"));
		assertEquals("cm", seriesMap.get("unit"));
	}

	@Test
	public void shouldPublishMessageForValidTrapAndMultipleOidMapping() {
		register.setAlarmMapping(alarmMapping);
		register.setEventMapping(eventMapping);

		// Action
		trapHandler.processPdu(event);

		verify(alarmPublisher, times(1)).publish(alarmCaptor.capture());
		verify(eventPublisher, times(1)).publish(eventCaptor.capture());
		verify(measurementPublisher, times(0)).publish(measurementCaptor.capture());

		AlarmRepresentation alarm = alarmCaptor.getValue();
		assertEquals("MINOR", alarm.getSeverity());
		assertEquals("c8y_SNMPAlarm", alarm.getType());
		assertEquals("Test SNMP alarm", alarm.getText());
		assertEquals(device1Mo.getId(), alarm.getSource().getId());

		EventRepresentation eventResult = eventCaptor.getValue();
		assertEquals("c8y_SNMPEvent", eventResult.getType());
		assertEquals("Test SNMP event", eventResult.getText());
		assertEquals(device1Mo.getId(), eventResult.getSource().getId());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldPublishMeasurementWithStaticFragmentsForValidTrapAndOidMeasurementMapping() {
		register.setMeasurementMapping(measurementMapping);
		
		Map<String, Map<?, ?>> staticFragmentsMap = new HashMap<>();
		staticFragmentsMap.put("c8y_StaticFragment", Collections.singletonMap("key1", "value1"));
		measurementMapping.setStaticFragmentsMap(staticFragmentsMap);
		
		// Action
		trapHandler.processPdu(event);

		verify(alarmPublisher, times(0)).publish(alarmCaptor.capture());
		verify(eventPublisher, times(0)).publish(eventCaptor.capture());
		verify(measurementPublisher, times(1)).publish(measurementCaptor.capture());



		MeasurementRepresentation measurement = measurementCaptor.getValue();
		assertEquals("c8y_SNMPMeasurement", measurement.getType());
		assertEquals(device1Mo.getId(), measurement.getSource().getId());

		Map<String, Object> measurementAttrs = measurement.getAttrs();
		assertTrue(measurementAttrs.containsKey(measurementMapping.getType()));
		Map<String, Map<String, Object>> typeMap = (Map<String, Map<String, Object>>)measurementAttrs.get(measurementMapping.getType());
		assertTrue(typeMap.containsKey(measurementMapping.getSeries()));

		Map<String, Object> seriesMap = typeMap.get(measurementMapping.getSeries());
		assertEquals(10, seriesMap.get("value"));
		assertEquals("cm", seriesMap.get("unit"));

		for(String oneStaticFragment : staticFragmentsMap.keySet()) {
			assertTrue(measurementAttrs.containsKey(oneStaticFragment));
		}
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

