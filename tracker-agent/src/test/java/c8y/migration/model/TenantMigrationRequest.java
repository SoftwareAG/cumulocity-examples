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

public class TenantMigrationRequest {

	private final String tenant;
	private final List<DeviceMigrationRequest> devices = new ArrayList<>();

	public TenantMigrationRequest(String tenant) {
		this.tenant = tenant;
	}

	public String getTenant() {
		return tenant;
	}

	@Override
	public String toString() {
		return "[tenant=" + tenant + ", devices=" + devices + "]";
	}

	public void add(DeviceMigrationRequest deviceMigrationRequest) {
		devices.add(deviceMigrationRequest);
	}
	
	public List<DeviceMigrationRequest> getDevices() {
		return devices;
	}


}
