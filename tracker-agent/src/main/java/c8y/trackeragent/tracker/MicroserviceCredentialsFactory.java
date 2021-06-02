package c8y.trackeragent.tracker;

import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MicroserviceCredentialsFactory {

    @Value("${C8Y.username}")
    private String username;

    @Value("${C8Y.password}")
    private String password;

    @Value("${C8Y.tenant}")
    private String tenant;

    public MicroserviceCredentials get() {
        return new MicroserviceCredentials(tenant, username, password, null, null, null, null);
    }

    public MicroserviceCredentials getForTenant(String tenant) {
        return new MicroserviceCredentials(tenant, username, password, null, null, null, null);
    }
}
