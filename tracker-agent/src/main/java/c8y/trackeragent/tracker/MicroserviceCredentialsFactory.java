package c8y.trackeragent.tracker;

import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MicroserviceCredentialsFactory {

    private final DeviceCredentialsRepository credentialsRepository;

    public MicroserviceCredentials getForTenant(String tenant) {
        DeviceCredentials agentCredentials = credentialsRepository.getAgentCredentials(tenant);
        return new MicroserviceCredentials(
                tenant, agentCredentials.getUsername(), agentCredentials.getPassword(),
                null, null, null, null
        );
    }
}
