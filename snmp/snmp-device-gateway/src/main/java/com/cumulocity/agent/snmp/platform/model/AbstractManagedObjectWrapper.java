package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import lombok.Getter;

import java.util.Map;

@Getter
public abstract class AbstractManagedObjectWrapper {

	protected ManagedObjectRepresentation managedObject;

	AbstractManagedObjectWrapper(ManagedObjectRepresentation managedObject) {
		this.managedObject = managedObject;
	}

	public GId getId() {
		return managedObject.getId();
	}

	public String getName() {
		return managedObject.getName();
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
