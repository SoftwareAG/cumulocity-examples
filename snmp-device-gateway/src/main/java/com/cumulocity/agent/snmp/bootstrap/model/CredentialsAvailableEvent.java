package com.cumulocity.agent.snmp.bootstrap.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CredentialsAvailableEvent {

	private final DeviceCredentials deviceCredentials;
}