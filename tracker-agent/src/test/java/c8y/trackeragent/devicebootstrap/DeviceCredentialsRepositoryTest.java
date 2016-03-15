package c8y.trackeragent.devicebootstrap;

import static c8y.trackeragent.devicebootstrap.DeviceBootstrapStatus.BOOTSTRAPED;
import static c8y.trackeragent.devicebootstrap.DeviceBootstrapStatus.WAITING_FOR_AGENT;
import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class DeviceCredentialsRepositoryTest {
	
	private static final String IMEI = "1234567890";
	private static final String TENANT = "tenant";
	
	private DeviceCredentialsRepository deviceCredentialsRepository;
	private DeviceCredentials deviceCredentials = DeviceCredentials.forDevice(IMEI, TENANT, BOOTSTRAPED);
	private DeviceCredentials agentCredentials = DeviceCredentials.forAgent(TENANT, "john", "secret");
	
	@Before
	public void init() throws Exception {
		File file = assureDevicePropertiesFile();
		deviceCredentialsRepository = new DeviceCredentialsRepository(file.getAbsolutePath());
		deviceCredentialsRepository.refresh();
	}

	private File assureDevicePropertiesFile() throws IOException {
		File file = new File("target/device.properties");
		FileUtils.deleteQuietly(file);
		file.createNewFile();
		return file;
	}
	
	@Test
	public void shouldSaveAndGetDeviceCredentials() throws Exception {
		deviceCredentialsRepository.saveDeviceCredentials(deviceCredentials);
		
		assertThat(deviceCredentialsRepository.getDeviceCredentials(IMEI)).isEqualTo(deviceCredentials);
	}
	
	@Test
	public void shouldSaveAndHasDeviceCredentials() throws Exception {
		deviceCredentialsRepository.saveDeviceCredentials(deviceCredentials);
		
		assertThat(deviceCredentialsRepository.hasDeviceCredentials(IMEI)).isTrue();
	}
	
	@Test
	public void shouldGetAllDeviceCredentials() throws Exception {
		deviceCredentialsRepository.saveDeviceCredentials(deviceCredentials);
		
		assertThat(deviceCredentialsRepository.getAllDeviceCredentials()).containsOnly(deviceCredentials);
	}
	
	@Test
	public void shouldSaveAndGetAgentCredentials() throws Exception {
		deviceCredentialsRepository.saveAgentCredentials(agentCredentials);
		
		assertThat(deviceCredentialsRepository.getAgentCredentials(TENANT)).isEqualTo(agentCredentials);
	}
	
	@Test
	public void shouldSaveAndHasAgentCredentials() throws Exception {
		deviceCredentialsRepository.saveAgentCredentials(agentCredentials);
		
		assertThat(deviceCredentialsRepository.hasAgentCredentials(TENANT)).isTrue();
	}
	
	@Test
	public void shouldGetAllAgentCredentials() throws Exception {
		deviceCredentialsRepository.saveAgentCredentials(agentCredentials);
		
		assertThat(deviceCredentialsRepository.getAllAgentCredentials()).containsOnly(agentCredentials);
	}
	
	@Test
	public void shouldRefreshDataFromFile() throws Exception {
		deviceCredentialsRepository.saveAgentCredentials(agentCredentials);
		deviceCredentialsRepository.saveDeviceCredentials(deviceCredentials);
		
		deviceCredentialsRepository.refresh();
		
		assertThat(deviceCredentialsRepository.getAllAgentCredentials()).containsOnly(agentCredentials);
		assertThat(deviceCredentialsRepository.getAllDeviceCredentials()).containsOnly(deviceCredentials);
	}
	
	@Test
	public void shouldSetAllDeviceCredentialsBootstraped() throws Exception {
		DeviceCredentials cred_1 = DeviceCredentials.forDevice("imei_1", "tenant_1", WAITING_FOR_AGENT);
		deviceCredentialsRepository.saveDeviceCredentials(cred_1);
		DeviceCredentials cred_2 = DeviceCredentials.forDevice("imei_2", "tenant_2", WAITING_FOR_AGENT);
		deviceCredentialsRepository.saveDeviceCredentials(cred_2);
		
		deviceCredentialsRepository.setAllDeviceCredentialsBootstraped("tenant_1");
		
		Iterable<DeviceCredentials> allDeviceCredentials = deviceCredentialsRepository.getAllDeviceCredentials("tenant_1");
		assertThat(allDeviceCredentials).containsOnly(DeviceCredentials.forDevice("imei_1", "tenant_1", BOOTSTRAPED));
	}

}
