package com.cumulocity.agent.snmp.platform.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class DeviceProtocolManagedObjectWrapper extends AbstractManagedObjectWrapper {

	public static final String C8Y_REGISTERS = "c8y_Registers";

	Map<String, Register> oidMap = new ConcurrentHashMap<>();

	public DeviceProtocolManagedObjectWrapper(ManagedObjectRepresentation deviceProtocolMO) {
		super(deviceProtocolMO);

		loadRegisters();
	}

	private void loadRegisters() {
		Object registersObj = managedObject.get(C8Y_REGISTERS);
		if (registersObj instanceof List) {
			ObjectMapper mapper = new ObjectMapper();
			List<Register> registers = mapper.convertValue(registersObj,
					mapper.getTypeFactory().constructCollectionType(ArrayList.class, Register.class));
			for (Register register : registers) {
				oidMap.put(register.getOid().toLowerCase(), register);
			}
		} else {
			log.info("Did not find {} fragment in the received gateway managed object {}", C8Y_REGISTERS,
					managedObject.getName());
		}
	}
}
