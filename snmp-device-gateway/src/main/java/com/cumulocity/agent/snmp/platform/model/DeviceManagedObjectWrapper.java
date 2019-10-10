package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.agent.snmp.utils.Constants;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceManagedObjectWrapper extends AbstractManagedObjectWrapper {

	private SnmpDeviceProperties properties;

	public DeviceManagedObjectWrapper(ManagedObjectRepresentation snmpDeviceMo) {
		super(snmpDeviceMo);

		loadProperties();
	}

	public List<String> getChildrenIDs() {
		return null;
	}

	private void loadProperties() {
		Object fragmentObj = managedObject.get(Constants.C8Y_SNMP_DEVICE);
		if (fragmentObj != null) {
			ObjectMapper mapper = new ObjectMapper();
			properties = mapper.convertValue(fragmentObj, SnmpDeviceProperties.class);
		} else {
			log.info("Did not find correct {} fragment in the received gateway managed object {}",
					Constants.C8Y_SNMP_DEVICE, managedObject.getName());
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
}
