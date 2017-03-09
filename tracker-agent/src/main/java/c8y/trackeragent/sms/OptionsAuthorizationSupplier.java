package c8y.trackeragent.sms;

import c8y.trackeragent.configuration.TrackerConfiguration;
import com.cumulocity.sms.client.SmsMessagingApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OptionsAuthorizationSupplier extends SmsMessagingApiImpl.SmsCredentialsProvider {

    private final ThreadLocal<String> tenantThreadLocal = new ThreadLocal<>();

    @Autowired
    private TrackerConfiguration configuration;

    public void optionsAuthForTenant(String tenant) {
        tenantThreadLocal.set(tenant);
    }

    @Override
    public String getTenant() {
        return tenantThreadLocal.get();
    }

    @Override
    public String getUsername() {
        return configuration.getSmsGatewayUser();
    }

    @Override
    public String getPassword() {
        return configuration.getSmsGatewayPassword();
    }
}
