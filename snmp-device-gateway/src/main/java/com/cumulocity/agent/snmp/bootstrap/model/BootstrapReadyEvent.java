package com.cumulocity.agent.snmp.bootstrap.model;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BootstrapReadyEvent {

	@Getter
	private ManagedObjectRepresentation gatewayDevice;
}