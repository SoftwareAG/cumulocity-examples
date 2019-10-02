package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.agent.snmp.utils.Constants;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class GatewayManagedObjectWrapper extends AbstractManagedObjectWrapper {

	public GatewayManagedObjectWrapper(ManagedObjectRepresentation gatewayMo) {
		super(gatewayMo);

		loadAttributes();
	}

	private SnmpCommunicationProperties snmpCommunicationAttrs;

	public List<String> getChildrenIDs() {
		return null;
	}

	public Object getAttributeValue(String fragmentName, String attributeKey) {
		Object attributeValue = null;

		Object fragmentObj = managedObject.getAttrs().get(fragmentName);
		if (fragmentObj instanceof Map) {
			Map<?, ?> fragmentMap = (Map<?, ?>) fragmentObj;
			attributeValue = fragmentMap.get(attributeKey);
		}

		return attributeValue;
	}

	private void loadAttributes() {
		Object fragmentObj = managedObject.get(Constants.C8Y_SNMP_GATEWAY);
		if (fragmentObj instanceof Map) {
			ObjectMapper mapper = new ObjectMapper();
			snmpCommunicationAttrs = mapper.convertValue(fragmentObj, SnmpCommunicationProperties.class);
		} else {
			log.info("Did not find correct {} fragment in the received gateway managed object {}",
					Constants.C8Y_SNMP_GATEWAY, managedObject.getName());
		}
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class SnmpCommunicationProperties {

		private String ipRange;

		private long pollingRate;

		private long transmitRate;

		private long autoDiscoveryInterval;
	}
}
