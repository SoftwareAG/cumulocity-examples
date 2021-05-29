/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.migration.service;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.migration.model.DeviceMigrationResponse;
import c8y.migration.model.MigrationException;
import c8y.migration.model.TenantMigrationResponse;
import c8y.trackeragent.utils.GroupPropertyAccessor;
import c8y.trackeragent.utils.GroupPropertyAccessor.Group;

public class MigrationResponseSerializer {

	private static final Logger logger = LoggerFactory.getLogger(MigrationResponseSerializer.class);
	private static final String OUTPUT_DIR = "/etc/tracker-agent-migration/output";

	private final GroupPropertyAccessor tenantAccessor;
	private final GroupPropertyAccessor deviceAccessor;
	private final File outputPath;

	public MigrationResponseSerializer(String otputFileName) {
		this.outputPath = tenantOutputFile(otputFileName);
		this.tenantAccessor = new GroupPropertyAccessor(outputPath.getPath(), asList("password", "user"));
		this.deviceAccessor = new GroupPropertyAccessor(outputPath.getPath(), asList("tenant"));
	}

	public void serialize(List<TenantMigrationResponse> responses) {
		logger.info("Serialize all responses into the file \n {}", outputPath);
		for (TenantMigrationResponse response : responses) {
			doSerialize(response);
		}
	}
	
	public void serialize(TenantMigrationResponse response) {
		logger.info("Serialize response: {} into the file \n {}", response, outputPath);
		doSerialize(response);
	}

	private void doSerialize(TenantMigrationResponse response) {
		String groupName = "tenant-" + response.getAgentOwner().getTenantId();
		Group tenantGroup = tenantAccessor.createEmptyGroup(groupName);
		tenantGroup.put("password", response.getAgentOwner().getPassword());
		tenantGroup.put("user", response.getAgentOwner().getUsername());
		tenantAccessor.write(tenantGroup);
		for (DeviceMigrationResponse deviceResponse : response.getDeviceResponses()) {
			Group deviceGroup = deviceAccessor.createEmptyGroup(deviceResponse.getImei());
			deviceGroup.put("tenantId", response.getTenant());
			deviceAccessor.write(deviceGroup);
		}
	}

	private File tenantOutputFile(String otputFileName) {
		try {
			File outputDir = new File(OUTPUT_DIR);
			Files.createDirectories(outputDir.toPath());
			File outputFile = new File(outputDir, otputFileName);
			outputFile.createNewFile();
			return outputFile;
		} catch (IOException e) {
			throw new MigrationException(e);
		}
	}
}
