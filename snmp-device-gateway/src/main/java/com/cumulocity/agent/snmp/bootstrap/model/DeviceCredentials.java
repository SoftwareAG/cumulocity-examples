package com.cumulocity.agent.snmp.bootstrap.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceCredentials implements Serializable {

	private static final long serialVersionUID = -1087717467058887687L;

	private String tenantId;

	private String username;

	private String password;
}