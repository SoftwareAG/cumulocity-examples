package com.cumulocity.agent.snmp.platform.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class PlatformConnectionReadyEvent {

	private String currentUser;
}