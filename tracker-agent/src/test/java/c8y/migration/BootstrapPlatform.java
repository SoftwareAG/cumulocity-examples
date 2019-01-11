package c8y.migration;

import com.cumulocity.model.authentication.CumulocityBasicCredentials;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BootstrapPlatform {

	private final Settings settings;
	private DeviceCredentialsApi deviceCredentialsApi;

	@Autowired
	public BootstrapPlatform(Settings settings) {
		this.settings = settings;
	}

	@PostConstruct
	public void init() {
		CumulocityBasicCredentials credentials = CumulocityBasicCredentials.builder()
		.tenantId(settings.getBootstrapTenant())
		.username(settings.getBootstrapUser())
		.password(settings.getBootstrapPassword())
		.build();
		PlatformImpl platform = new PlatformImpl(settings.getC8yHost(), credentials);
		deviceCredentialsApi = platform.getDeviceCredentialsApi();
	}

	public DeviceCredentialsApi getDeviceCredentialsApi() {
		return deviceCredentialsApi;
	}

}
