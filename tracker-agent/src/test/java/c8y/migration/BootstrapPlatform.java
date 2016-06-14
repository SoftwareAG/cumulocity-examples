package c8y.migration;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;

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
		CumulocityCredentials credentials = cumulocityCredentials(settings.getBootstrapUser(),
				settings.getBootstrapPassword()).withTenantId(settings.getBootstrapTenant()).build();
		PlatformImpl platform = new PlatformImpl(settings.getC8yHost(), credentials);
		deviceCredentialsApi = platform.getDeviceCredentialsApi();
	}

	public DeviceCredentialsApi getDeviceCredentialsApi() {
		return deviceCredentialsApi;
	}

}
