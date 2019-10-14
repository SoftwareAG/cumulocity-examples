package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.agent.snmp.utils.Constants;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
public class DeviceProtocolManagedObjectWrapper extends AbstractManagedObjectWrapper {

	Map<String, Register> oidMap = new ConcurrentHashMap<>();

	public DeviceProtocolManagedObjectWrapper(ManagedObjectRepresentation deviceProtocolMO) {
		super(deviceProtocolMO);

		loadRegisters();
	}

	private void loadRegisters() {
		Object registersObj = managedObject.get(Constants.C8Y_REGISTERS);
		if (registersObj instanceof List) {
			ObjectMapper mapper = new ObjectMapper();
			List<Register> registers = mapper.convertValue(registersObj,
					mapper.getTypeFactory().constructCollectionType(ArrayList.class, Register.class));
			for (Register register : registers) {
				oidMap.put(register.getOid().toLowerCase(), register);
			}
		} else {
			log.info("Did not find {} fragment in the received gateway managed object {}", Constants.C8Y_REGISTERS,
					managedObject.getName());
		}
	}
}
