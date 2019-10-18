package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class GatewayManagedObjectWrapper extends AbstractManagedObjectWrapper {

    public static final String C8Y_SNMP_GATEWAY_TYPE = "c8y_SNMP";
	public static final String C8Y_EXTERNAL_ID_TYPE = "c8y_Serial";
	public static final String C8Y_SNMP_GATEWAY = "c8y_SNMPGateway";
	public static final String C8Y_SUPPORTED_OPERATIONS = "c8y_SNMPConfiguration";

	public final String childDevicesPath;

	private SnmpCommunicationProperties SnmpCommunicationProperties;


	public GatewayManagedObjectWrapper(ManagedObjectRepresentation gatewayMo) {
		super(gatewayMo);

		this.childDevicesPath = UriBuilder.fromPath("/inventory/managedObjects/{deviceId}/childDevices").build(getId().toString()).getPath();

		loadSnmpCommunicationProperties();
	}

	public List<String> getChildrenIDs() {
		return null;
	}

	private void loadSnmpCommunicationProperties() {
		Object fragmentObj = managedObject.get(C8Y_SNMP_GATEWAY);
		if (fragmentObj instanceof Map) {
			ObjectMapper mapper = new ObjectMapper();
			SnmpCommunicationProperties = mapper.convertValue(fragmentObj, SnmpCommunicationProperties.class);
		} else {
			log.info("Did not find correct {} fragment in the received gateway managed object {}",
					C8Y_SNMP_GATEWAY, managedObject.getName());
		}
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class SnmpCommunicationProperties {

		private String ipRange;

		private long pollingRate;

		private long transmitRate;

		private long autoDiscoveryInterval;
		
		public long getPollingRateInMinutes() {
			return pollingRate;
		}
	}
}
