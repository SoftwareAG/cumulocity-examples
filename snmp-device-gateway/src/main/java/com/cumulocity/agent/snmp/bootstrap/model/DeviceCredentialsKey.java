package com.cumulocity.agent.snmp.bootstrap.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public final class DeviceCredentialsKey implements Serializable {

	private static final long serialVersionUID = 1388522837251535065L;

	private final String url;

	private final String tenantId;

	private final String username;
}
