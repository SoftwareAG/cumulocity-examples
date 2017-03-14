package c8y.trackeragent.sms;

import c8y.trackeragent.configuration.TrackerConfiguration;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import com.cumulocity.sms.client.SmsMessagingApiImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OptionsAuthorizationSupplier extends SmsMessagingApiImpl.SmsCredentialsProvider {

    private final ThreadLocal<DeviceCredentials> credentials = new ThreadLocal<>();

    @Autowired
    private TrackerConfiguration trackerConfiguration;

    public void set(DeviceCredentials tenant) {
        credentials.set(tenant);
    }

    public void clear() {
        credentials.remove();
    }

    @Override
    public String getTenant() {
        return credentials.get().getTenant();
    }

    @Override
    public String getUsername() {
        return trackerConfiguration.getSmsGatewayUser();
    }

    @Override
    public String getPassword() {
        return trackerConfiguration.getSmsGatewayPassword();
    }
}
