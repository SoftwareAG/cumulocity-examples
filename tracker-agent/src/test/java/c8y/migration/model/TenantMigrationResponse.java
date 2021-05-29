/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.migration.model;

import java.util.ArrayList;
import java.util.List;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;

public class TenantMigrationResponse {
	
	private DeviceCredentialsRepresentation agentOwner;
	private final List<DeviceMigrationResponse> deviceResponses = new ArrayList<DeviceMigrationResponse>();
	private final String tenant;
	
	public TenantMigrationResponse(String tenant) {
		this.tenant = tenant;
	}

	public DeviceCredentialsRepresentation getAgentOwner() {
		return agentOwner;
	}

	public void setAgentOwner(DeviceCredentialsRepresentation agentOwner) {
		this.agentOwner = agentOwner;
	}

	public List<DeviceMigrationResponse> getDeviceResponses() {
		return deviceResponses;
	}
	
	public String getTenant() {
		return tenant;
	}

	@Override
	public String toString() {
		return "[agentOwner=" + agentOwner + ", deviceResponses=" + deviceResponses + "]";
	}
}
