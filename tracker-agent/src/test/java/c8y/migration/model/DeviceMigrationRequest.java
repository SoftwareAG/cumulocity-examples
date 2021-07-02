/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.migration.model;

public class DeviceMigrationRequest {

	private final String imei;
	private final String user;
	private final String password;

	public DeviceMigrationRequest(String imei, String user, String password) {
		this.imei = imei;
		this.user = user;
		this.password = password;
	}

	public String getImei() {
		return imei;
	}
	
	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "DeviceMigrationRequest [imei=" + imei + ", user=" + user + ", password=" + password + "]";
	}
}
