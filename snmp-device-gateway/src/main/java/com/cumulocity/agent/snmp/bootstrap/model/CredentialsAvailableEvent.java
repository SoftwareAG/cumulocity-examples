package com.cumulocity.agent.snmp.bootstrap.model;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CredentialsAvailableEvent {

	private final DeviceCredentialsRepresentation deviceCredentials;
}