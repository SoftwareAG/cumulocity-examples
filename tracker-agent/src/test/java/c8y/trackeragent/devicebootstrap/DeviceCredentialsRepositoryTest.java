package c8y.trackeragent.devicebootstrap;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.exception.UnknownDeviceException;
import c8y.trackeragent.exception.UnknownTenantException;

public class DeviceCredentialsRepositoryTest {
	
	private static final String IMEI = "1234567890";
	private static final String TENANT = "tenant";
	
	private DeviceCredentialsRepository credentialsRepository;
	private DeviceCredentials deviceCredentials = DeviceCredentials.forDevice(IMEI, TENANT);
	private DeviceCredentials agentCredentials = DeviceCredentials.forAgent(TENANT, "john", "secret");
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
	public void findAgentCredentials() throws Exception {
		FileUtils.writeLines(file, asList("tenant-management.password=secret123", "tenant-management.user=device_tracker-agent-management"));
		credentialsRepository.refresh();
		
		credentialsRepository.getAgentCredentials("management");
	}
	
	@Test
	public void shouldSaveAndGetDeviceCredentials() throws Exception {
		credentialsRepository.saveDeviceCredentials(deviceCredentials);
		
		assertThat(credentialsRepository.getDeviceCredentials(IMEI)).isEqualTo(deviceCredentials);
	}
	
	@Test
	public void shouldSaveAndHasDeviceCredentials() throws Exception {
		credentialsRepository.saveDeviceCredentials(deviceCredentials);
		
		assertThat(credentialsRepository.hasDeviceCredentials(IMEI)).isTrue();
	}
	
	@Test
	public void shouldGetAllDeviceCredentials() throws Exception {
		credentialsRepository.saveDeviceCredentials(deviceCredentials);
		
		assertThat(credentialsRepository.getAllDeviceCredentials()).containsOnly(deviceCredentials);
	}
	
	@Test
	public void shouldSaveAndGetAgentCredentials() throws Exception {
		credentialsRepository.saveAgentCredentials(agentCredentials);
		
		assertThat(credentialsRepository.getAgentCredentials(TENANT)).isEqualTo(agentCredentials);
	}
	
	@Test
	public void shouldSaveAndHasAgentCredentials() throws Exception {
		credentialsRepository.saveAgentCredentials(agentCredentials);
		
		assertThat(credentialsRepository.hasAgentCredentials(TENANT)).isTrue();
	}
	
	@Test
	public void shouldGetAllAgentCredentials() throws Exception {
		credentialsRepository.saveAgentCredentials(agentCredentials);
		
		assertThat(credentialsRepository.getAllAgentCredentials()).containsOnly(agentCredentials);
	}
	
	@Test
	public void shouldRefreshDataFromFile() throws Exception {
		credentialsRepository.saveAgentCredentials(agentCredentials);
		credentialsRepository.saveDeviceCredentials(deviceCredentials);
		
		credentialsRepository.refresh();
		
		assertThat(credentialsRepository.getAllAgentCredentials()).containsOnly(agentCredentials);
		assertThat(credentialsRepository.getAllDeviceCredentials()).containsOnly(deviceCredentials);
	}
	
	@Test
	public void shouldGetAllDeviceCredentialsForTenant() throws Exception {
		DeviceCredentials cred_1 = DeviceCredentials.forDevice("imei_1", "tenant_1");
		credentialsRepository.saveDeviceCredentials(cred_1);
		DeviceCredentials cred_2 = DeviceCredentials.forDevice("imei_2", "tenant_2");
		credentialsRepository.saveDeviceCredentials(cred_2);
		
		credentialsRepository.getAllDeviceCredentials("tenant_1");
		
		Iterable<DeviceCredentials> allDeviceCredentials = credentialsRepository.getAllDeviceCredentials("tenant_1");
		assertThat(allDeviceCredentials).containsOnly(cred_1);
	}
	
	@Test(expected = UnknownDeviceException.class)
	public void shouldThrowExceptionDeviceCredentialsAbsent() throws Exception {
		DeviceCredentials deviceCredentials = credentialsRepository.getDeviceCredentials(IMEI);
		
		assertThat(deviceCredentials).isNull();
	}
	
	@Test(expected = UnknownTenantException.class)
	public void shouldThrowExceptionTenantCredentialsAbsent() throws Exception {
		DeviceCredentials deviceCredentials = credentialsRepository.getAgentCredentials(TENANT);
		
		assertThat(deviceCredentials).isNull();
	}

}
