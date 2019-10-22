package com.cumulocity.agent.snmp.device.service;

import com.cumulocity.agent.snmp.platform.model.*;
import com.cumulocity.agent.snmp.platform.pubsub.publisher.AlarmPublisher;
import com.cumulocity.agent.snmp.platform.pubsub.publisher.EventPublisher;
import com.cumulocity.agent.snmp.platform.pubsub.publisher.MeasurementPublisher;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.agent.snmp.util.IpAddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.smi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

@Slf4j
@Component
public class TrapHandler implements CommandResponder {

	/**
	 * SNMP constants used during resolving Opaque variable
	 */
	private static final byte TAG1 = (byte) 0x9f;

	private static final byte TAG_FLOAT = (byte) 0x78;

	private static final byte TAG_DOUBLE = (byte) 0x79;


	@Autowired
	private GatewayDataProvider dataProvider;

	@Autowired
	private AlarmPublisher alarmPublisher;

	@Autowired
	private EventPublisher eventPublisher;

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
			log.error("Failed to translate received trap.\n{}", event);
			return;
		}

		if (!dataProvider.getDeviceProtocolMap().containsKey(deviceIp)) {
			log.error("Trap received from an unknown device with IP address : {}", deviceIp);
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

		try {
			return IpAddressUtil.sanitizeIpAddress(peerAddress.toString());
		} catch(IllegalArgumentException iae) {
			log.warn("Error while parsing the IP Address from the received trap.\n{}", event, iae);
			return null;
		}
	}

	private void handleUnknownDevice(String deviceIp) {
		AlarmMapping alarmMapping = new AlarmMapping();
		alarmMapping.setSeverity(AlarmSeverity.MAJOR.name());
		alarmMapping.setType(AlarmMapping.c8y_TRAPReceivedFromUnknownDevice);
		alarmMapping.setText("Trap received from an unknown device with IP address : " + deviceIp);

		alarmPublisher.publish(alarmMapping.buildAlarmRepresentation(dataProvider.getGatewayDevice().getManagedObject()));
	}

	protected void processDevicePdu(String deviceIp, PDU pdu) {
		if (pdu.getVariableBindings() == null || pdu.getVariableBindings().size() == 0) {
			log.debug("No OID found in the received trap");
			return;
		}

		Map<String, DeviceManagedObjectWrapper> deviceProtocolMap = dataProvider.getDeviceProtocolMap();
		DeviceManagedObjectWrapper deviceMo = deviceProtocolMap.get(deviceIp);
		String deviceProtocol = deviceMo.getDeviceProtocol();
		DeviceProtocolManagedObjectWrapper deviceProtocolWrapper = dataProvider.getProtocolMap().get(deviceProtocol);

		if (deviceProtocolWrapper == null) {
			log.error("{} device procotol object not found at the gateway for the device with IP address {}",
					deviceProtocol, deviceIp);
			return;
		}

		boolean isMappingFound = false;
		Map<OID, Register> oidMap = deviceProtocolWrapper.getOidMap();

		for (VariableBinding binding : pdu.getVariableBindings()) {
			OID oid = binding.getOid();
			if (oidMap.containsKey(oid)) {
				Register register = oidMap.get(oid);

				if(register.getAlarmMapping() != null) {
					alarmPublisher.publish(register.getAlarmMapping().buildAlarmRepresentation(deviceMo.getManagedObject()));
				}

				if(register.getEventMapping() != null) {
					eventPublisher.publish(register.getEventMapping().buildEventRepresentation(deviceMo.getManagedObject()));
				}

				if(register.getMeasurementMapping() != null) {
					measurementPublisher.publish(register.getMeasurementMapping().buildMeasurementRepresentation(deviceMo.getManagedObject(), parse(binding.getVariable()), register.getUnit()));
				}

				isMappingFound = true;
			} else {
				log.warn("{} OID could not be found in the {} device protocol selected for the device {}", binding.getOid(), deviceProtocol, deviceIp);
			}
		}

		if (!isMappingFound) {
			log.debug("No configuration mappings found for received trap from the device {}", deviceMo.getProperties().getIpAddress());
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
				retvalue = ((IpAddress) valueAsVar).getInetAddress().getHostAddress();
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

			if (t1 == TAG1) {
				if (t2 == TAG_FLOAT && l == 4) {
					value = bais.getFloat();
				} else if (t2 == TAG_DOUBLE && l == 8) {
					value = bais.getDouble();
				}
			}
		} catch (IOException e) {
			log.error("Error while resolving {} Opaque object", var.toString());
		}

		return value;
	}
}
