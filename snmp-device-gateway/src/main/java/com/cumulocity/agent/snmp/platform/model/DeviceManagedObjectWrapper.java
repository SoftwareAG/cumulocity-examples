package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceManagedObjectWrapper extends AbstractManagedObjectWrapper {

	public static final String C8Y_SNMP_DEVICE = "c8y_SNMPDevice";

	private SnmpDeviceProperties properties;

	public DeviceManagedObjectWrapper(ManagedObjectRepresentation snmpDeviceMo) {
		super(snmpDeviceMo);

		loadProperties();
	}

	private void loadProperties() {
		Object fragmentObj = managedObject.get(C8Y_SNMP_DEVICE);
		if (fragmentObj != null) {
			ObjectMapper mapper = new ObjectMapper();
			properties = mapper.convertValue(fragmentObj, SnmpDeviceProperties.class);
		} else {
			log.warn("Did not find correct {} fragment in the received gateway managed object {}",
					C8Y_SNMP_DEVICE, managedObject.getName());
			properties = new SnmpDeviceProperties();
		}
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class SnmpDeviceProperties {
		public static final String PROTOCOL_UDP = "UDP";
		public static final String PROTOCOL_TCP = "TCP";

		private int version;

		private String port;

		private String type;
		
		private String protocol;

		private String ipAddress;

		private DeviceAuthentication auth;

		public DeviceAuthentication getAuth() {
			if(auth == null) {
				auth = new DeviceAuthentication();
			}

			return auth;
		}

		public boolean isProtocolUdp() {
			return Strings.isNullOrEmpty(protocol) || PROTOCOL_UDP.equalsIgnoreCase(protocol);
		}

		public boolean isProtocolTcp() {
			return PROTOCOL_TCP.equalsIgnoreCase(protocol);
		}
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class DeviceAuthentication {

		private String engineId;

		private String username;

		private String privPassword;

		private String authPassword;

		private int authProtocol;

		private int privProtocol;

		private int securityLevel;
	}

	public String getDeviceProtocol() {
		if(properties.getType() == null) {
			return null;
		}

		String[] data = properties.getType().trim().split("/");
		return data[data.length - 1];
	}
}
