/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.migration.steps;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import c8y.migration.model.TenantMigrationRequest;
import c8y.migration.model.TenantMigrationResponse;
import c8y.migration.service.MigrationResponseSerializer;

@Component
@Order(value = 30)
public class SerializeTenanCredentialsStep extends MigrationStep {

	@Override
	public void execute(TenantMigrationRequest req, TenantMigrationResponse response) {
		String otputFileName = "device-" + response.getTenant() + ".properties";
		new MigrationResponseSerializer(otputFileName).serialize(response);
	}
	
	

}
