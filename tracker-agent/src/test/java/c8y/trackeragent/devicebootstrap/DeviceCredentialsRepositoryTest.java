package c8y.trackeragent.devicebootstrap;

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
		DeviceCredentials credentials = DeviceCredentials.forDevice(IMEI, TENANT);
		
		deviceCredentialsRepository.saveDeviceCredentials(credentials);
		
		assertThat(deviceCredentialsRepository.getDeviceCredentials(IMEI)).isEqualTo(credentials);
	}
	
	@Test
	public void shouldSaveAndHasDeviceCredentials() throws Exception {
		DeviceCredentials credentials = DeviceCredentials.forDevice(IMEI, TENANT);
		
		deviceCredentialsRepository.saveDeviceCredentials(credentials);
		
		assertThat(deviceCredentialsRepository.hasDeviceCredentials(IMEI)).isTrue();
	}
	
	@Test
	public void shouldGetAllDeviceCredentials() throws Exception {
		DeviceCredentials credentials = DeviceCredentials.forDevice(IMEI, TENANT);
		
		deviceCredentialsRepository.saveDeviceCredentials(credentials);
		
		assertThat(deviceCredentialsRepository.getAllDeviceCredentials()).contains(credentials);
	}
	
	@Test
	public void shouldSaveAndGetAgentCredentials() throws Exception {
		DeviceCredentials credentials = DeviceCredentials.forAgent(TENANT, "john", "secret");
		
		deviceCredentialsRepository.saveAgentCredentials(credentials);
		
		assertThat(deviceCredentialsRepository.getDeviceCredentials(IMEI)).isEqualTo(credentials);
	}
	
	@Test
	public void shouldSaveAndHasAgentCredentials() throws Exception {
		DeviceCredentials credentials = DeviceCredentials.forAgent(TENANT, "john", "secret");
		
		deviceCredentialsRepository.saveAgentCredentials(credentials);
		
		assertThat(deviceCredentialsRepository.hasAgentCredentials(IMEI)).isTrue();
	}
	
	@Test
	public void shouldGetAllAgentCredentials() throws Exception {
		DeviceCredentials credentials = DeviceCredentials.forAgent(TENANT, "john", "secret");
		
		deviceCredentialsRepository.saveAgentCredentials(credentials);
		
		assertThat(deviceCredentialsRepository.getAllDeviceCredentials()).contains(credentials);
	}

}
