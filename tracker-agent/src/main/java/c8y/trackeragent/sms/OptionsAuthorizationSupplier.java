package c8y.trackeragent.sms;

import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import com.cumulocity.sms.client.SmsMessagingApiImpl;
import org.springframework.stereotype.Component;

@Component
public class OptionsAuthorizationSupplier extends SmsMessagingApiImpl.SmsCredentialsProvider {

    private final ThreadLocal<DeviceCredentials> credentials = new ThreadLocal<>();

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
        return credentials.get().getUsername();
    }

    @Override
    public String getPassword() {
        return credentials.get().getPassword();
    }
}
