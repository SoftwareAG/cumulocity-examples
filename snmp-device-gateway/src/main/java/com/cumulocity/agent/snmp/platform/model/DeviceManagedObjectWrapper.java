package com.cumulocity.agent.snmp.platform.model;

import java.util.List;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	public List<String> getChildrenIDs() {
		return null;
	}

	private void loadProperties() {
		Object fragmentObj = managedObject.get(C8Y_SNMP_DEVICE);
		if (fragmentObj != null) {
			ObjectMapper mapper = new ObjectMapper();
			properties = mapper.convertValue(fragmentObj, SnmpDeviceProperties.class);
		} else {
			log.info("Did not find correct {} fragment in the received gateway managed object {}",
					C8Y_SNMP_DEVICE, managedObject.getName());
		}
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class SnmpDeviceProperties {

		private int version;

		private String port;

		private String type;

		private String ipAddress;

		private DeviceAuthentication auth;
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
		String[] data = properties.getType().trim().split("/");
		String deviceProtocol = data[data.length - 1];
		return deviceProtocol;
	}
}
