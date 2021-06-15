package c8y.trackeragent.tracker;

import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MicroserviceCredentialsFactory {

    private final DeviceCredentialsRepository credentialsRepository;

    @Value("${application.key}")
    private String applicationKey;

    public MicroserviceCredentials getForTenant(String tenant) {
        DeviceCredentials agentCredentials = credentialsRepository.getAgentCredentials(tenant);
        log.info("Getting credentials for tenant {} and user {}.", tenant, agentCredentials.getUsername());
        return new MicroserviceCredentials(
                tenant, agentCredentials.getUsername(), agentCredentials.getPassword(),
                null, null, null, applicationKey
        );
    }
}
