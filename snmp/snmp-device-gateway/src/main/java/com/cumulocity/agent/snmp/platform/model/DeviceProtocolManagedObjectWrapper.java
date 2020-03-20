package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class DeviceProtocolManagedObjectWrapper extends AbstractManagedObjectWrapper {

	public static final String C8Y_REGISTERS = "c8y_Registers";

	Map<OID, Register> oidMap = new HashMap<>();

	List<VariableBinding> measurementVariableBindingList = new ArrayList<>();

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
				try {
					OID oid = new OID(register.getOid());
					oidMap.put(oid, register);

					if (register.getMeasurementMapping() != null) {
						VariableBinding variableBinding = new VariableBinding(oid);
						measurementVariableBindingList.add(variableBinding);
					}
				} catch(Throwable t) {
					log.error("Error while parsing the OID {} from the device protocol {}. Skipping the register.", register.getOid(), getName(), t);
				}
			}
		} else {
			log.warn("Did not find {} fragment in the received gateway managed object {}", C8Y_REGISTERS,
					managedObject.getName());
		}
	}
}
