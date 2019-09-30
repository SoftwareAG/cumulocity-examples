package com.cumulocity.agent.snmp.platform.model;

import java.util.Map;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import lombok.Getter;

@Getter
public abstract class AbstractManagedObjectWrapper {

	protected ManagedObjectRepresentation managedObject;

	public AbstractManagedObjectWrapper(ManagedObjectRepresentation managedObject) {
		this.managedObject = managedObject;
	}

	public GId getId() {
		return managedObject.getId();
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
}
