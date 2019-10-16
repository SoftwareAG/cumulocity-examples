package com.cumulocity.agent.snmp.client.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import org.joda.time.DateTime;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.smi.AbstractVariable;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Opaque;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.snmp.platform.model.AlarmMapping;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.DeviceProtocolManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.EventMapping;
import com.cumulocity.agent.snmp.platform.model.MeasurementMapping;
import com.cumulocity.agent.snmp.platform.model.Register;
import com.cumulocity.agent.snmp.platform.pubsub.publisher.AlarmPublisher;
import com.cumulocity.agent.snmp.platform.pubsub.publisher.EventPublisher;
import com.cumulocity.agent.snmp.platform.pubsub.publisher.MeasurementPublisher;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.agent.snmp.utils.Constants;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TrapHandler implements CommandResponder {

	@Autowired
	private AlarmPublisher alarmPublisher;

	@Autowired
	private EventPublisher eventPublisher;

	@Autowired
	private GatewayDataProvider dataProvider;

	@Autowired
	private MeasurementPublisher measurementPublisher;

	@Override
	public void processPdu(CommandResponderEvent event) {
		PDU pdu = event.getPDU();
		if (pdu == null) {
			log.error("No data present in the received trap");
			return;
		}

		String deviceIp = getDeviceIp(event);
		if (deviceIp == null) {
			log.error("Failed to translate peer address {}", event);
			return;
		}

		if (!dataProvider.getDeviceProtocolMap().containsKey(deviceIp)) {
			log.error("Trap received from an unknown device with '{}' IP address", deviceIp);
			handleUnknownDevice(deviceIp);
			return;
		}

		log.debug("Processing received trap from {} device : {}", deviceIp, pdu);
		processDevicePdu(deviceIp, pdu);

		event.setProcessed(true);
	}

	private String getDeviceIp(CommandResponderEvent event) {
		Address peerAddress = event.getPeerAddress();
		if (peerAddress == null || !peerAddress.isValid()) {
			return null;
		}

		return peerAddress.toString().split("/")[0].toLowerCase();
	}

	private void handleUnknownDevice(String deviceIp) {
		AlarmMapping alarmMapping = new AlarmMapping();
		alarmMapping.setSeverity("MAJOR");
		alarmMapping.setType("c8y_TRAPReceivedFromUnknownDevice");
		alarmMapping.setText("Trap received from an unknown device with IP address : " + deviceIp);

		process(alarmMapping, dataProvider.getGatewayDevice().getManagedObject());
	}

	private void processDevicePdu(String deviceIp, PDU pdu) {
		if (pdu.getVariableBindings() == null || pdu.getVariableBindings().size() == 0) {
			log.debug("No OID found in the received trap");
			return;
		}

		Map<String, DeviceManagedObjectWrapper> deviceProtocolMap = dataProvider.getDeviceProtocolMap();
		DeviceManagedObjectWrapper deviceMo = deviceProtocolMap.get(deviceIp);
		String deviceProtocol = deviceMo.getDeviceProtocol();
		DeviceProtocolManagedObjectWrapper deviceProtocolWrapper = dataProvider.getProtocolMap().get(deviceProtocol);

		if (deviceProtocolWrapper == null) {
			log.error("{} device procotol object not found at the gateway for the {} device", deviceProtocol, deviceIp);
			return;
		}

		boolean isMappingFound = false;
		Map<String, Register> oidMap = deviceProtocolWrapper.getOidMap();

		for (VariableBinding binding : pdu.getVariableBindings()) {
			String oid = binding.getOid().toString();
			if (oidMap.containsKey(oid)) {
				Register register = oidMap.get(oid);

				process(register.getAlarmMapping(), deviceMo.getManagedObject());
				process(register.getEventMapping(), deviceMo.getManagedObject());
				process(binding, register, deviceMo.getManagedObject());

				isMappingFound = true;
			} else {
				log.error("{} OID could not be found in the {} device protocol selected for the device {}",
						binding.getOid(), deviceProtocol, deviceIp);
			}
		}

		if (!isMappingFound) {
			log.debug("No configuration mappings found for received trap from the device {}",
					deviceMo.getProperties().getIpAddress());
		}
	}

	private void process(AlarmMapping alarmMapping, ManagedObjectRepresentation managedObject) {
		if (alarmMapping != null) {
			AlarmRepresentation newAlarm = new AlarmRepresentation();
			newAlarm.setSource(managedObject);
			newAlarm.setDateTime(DateTime.now());
			newAlarm.setType(alarmMapping.getType());
			newAlarm.setText(alarmMapping.getText());
			newAlarm.setSeverity(alarmMapping.getSeverity());

			alarmPublisher.publish(newAlarm);
		}
	}

	private void process(EventMapping eventMapping, ManagedObjectRepresentation managedObject) {
		if (eventMapping != null) {
			EventRepresentation newEvent = new EventRepresentation();
			newEvent.setSource(managedObject);
			newEvent.setDateTime(DateTime.now());
			newEvent.setType(eventMapping.getType());
			newEvent.setText(eventMapping.getText());

			eventPublisher.publish(newEvent);
		}
	}

	private void process(VariableBinding binding, Register register, ManagedObjectRepresentation managedObject) {
		MeasurementMapping measurementMapping = register.getMeasurementMapping();

		if (measurementMapping != null) {
			MeasurementRepresentation newMeasurement = new MeasurementRepresentation();
			newMeasurement.setSource(managedObject);
			newMeasurement.setDateTime(DateTime.now());
			newMeasurement.setType(measurementMapping.getType());

			Map<String, Object> series = Maps.newHashMap();
			String seriesKey = measurementMapping.getSeries().replace(" ", "_");

			Map<String, Object> type = Maps.newHashMap();
			String typeKey = measurementMapping.getType().replace(" ", "_");

			series.put("value", parse(binding.getVariable()));
			series.put("unit", register.getUnit());

			type.put(seriesKey, series);

			newMeasurement.setProperty(typeKey, type);

			Map<String, Map<?, ?>> staticFragmentsMap = measurementMapping.getStaticFragmentsMap();
			if (staticFragmentsMap != null && !staticFragmentsMap.isEmpty()) {
				newMeasurement.getAttrs().putAll(staticFragmentsMap);
			}

			measurementPublisher.publish(newMeasurement);
		}
	}

	private Object parse(Variable valueAsVar) {
		Object retvalue = null;

		if (valueAsVar != null) {
			if (valueAsVar instanceof OID) {
				retvalue = valueAsVar;
			} else if (valueAsVar instanceof UnsignedInteger32) {
				if (valueAsVar instanceof TimeTicks) {
					long epochcentisecond = valueAsVar.toLong();
					retvalue = epochcentisecond / 100.0;
				} else {
					retvalue = valueAsVar.toLong();
				}
			} else if (valueAsVar instanceof Integer32) {
				retvalue = valueAsVar.toInt();
			} else if (valueAsVar instanceof Counter64) {
				retvalue = valueAsVar.toLong();
			} else if (valueAsVar instanceof OctetString) {
				if (valueAsVar instanceof Opaque) {
					retvalue = resolveOpaque((Opaque) valueAsVar);
				} else {
					// It might be a C string, try to remove the last 0;
					// But only if the new string is printable
					OctetString octetVar = (OctetString) valueAsVar;
					int length = octetVar.length();
					if (length > 1 && octetVar.get(length - 1) == 0) {
						OctetString newVar = octetVar.substring(0, length - 1);
						if (newVar.isPrintable()) {
							valueAsVar = newVar;
							log.debug("Convertion an octet stream from " + octetVar + " to " + valueAsVar);
						}
					}
					retvalue = valueAsVar.toString();
				}
			} else if (valueAsVar instanceof IpAddress) {
				retvalue = ((IpAddress) valueAsVar).getInetAddress();
			} else if (valueAsVar instanceof Null) {
				// Nothing to do here
			} else {
				log.warn("Unknown syntax " + AbstractVariable.getSyntaxString(valueAsVar.getSyntax()));
			}
		}

		return retvalue;
	}

	private Object resolveOpaque(Opaque var) {
		// If not resolved, we will return the data as an array of bytes
		Object value = var.getValue();

		try {
			byte[] bytesArray = var.getValue();
			ByteBuffer bais = ByteBuffer.wrap(bytesArray);
			BERInputStream beris = new BERInputStream(bais);
			int l = BER.decodeLength(beris);

			byte t1 = bais.get();
			byte t2 = bais.get();

			if (t1 == Constants.TAG1) {
				if (t2 == Constants.TAG_FLOAT && l == 4) {
					value = bais.getFloat();
				} else if (t2 == Constants.TAG_DOUBLE && l == 8) {
					value = bais.getDouble();
				}
			}
		} catch (IOException e) {
			log.error("Error while resolving {} Opaque object", var.toString());
		}

		return value;
	}
}
