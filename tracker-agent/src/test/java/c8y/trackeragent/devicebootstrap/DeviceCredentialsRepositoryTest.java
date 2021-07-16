/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.devicebootstrap;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DeviceCredentialsRepositoryTest {
	
	private static final String IMEI = "1234567890";
	private static final String TENANT = "tenant";
	
	private DeviceCredentialsRepository credentialsRepository;
	private DeviceCredentials deviceCredentials = DeviceCredentials.forDevice(IMEI, TENANT);
	private File file;
	
	@Before
	public void init() throws Exception {
		file = assureDevicePropertiesFile();
		credentialsRepository = new DeviceCredentialsRepository(file.getAbsolutePath());
		credentialsRepository.refresh();
	}

	private File assureDevicePropertiesFile() throws IOException {
		File file = new File("target/device.properties");
		FileUtils.deleteQuietly(file);
		return file;
	}

	@Test
	public void shouldRefreshDataFromFile() throws Exception {
		//given
		credentialsRepository.saveDeviceCredentials(deviceCredentials);

		//when
		credentialsRepository.refresh();

		//then
		assertThat(credentialsRepository.getAllDeviceCredentials()).containsOnly(deviceCredentials);
	}
	
	@Test
	public void shouldGetAllDeviceCredentialsForTenant() {
		//given
		DeviceCredentials cred_1 = DeviceCredentials.forDevice("imei_1", "tenant_1");
		credentialsRepository.saveDeviceCredentials(cred_1);
		DeviceCredentials cred_2 = DeviceCredentials.forDevice("imei_2", "tenant_2");
		credentialsRepository.saveDeviceCredentials(cred_2);

		//when
		List<DeviceCredentials> allDeviceCredentials = credentialsRepository.getAllDeviceCredentials();

		//then
		assertThat(allDeviceCredentials.size()).isEqualTo(2);
		assertThat(allDeviceCredentials).containsOnly(cred_1, cred_2);
	}
}
