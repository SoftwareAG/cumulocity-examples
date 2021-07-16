package c8y.trackeragent.devicebootstrap;

import c8y.trackeragent.exception.TenantNotSubscribedException;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.sdk.client.SDKException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.Callable;

@Slf4j
@Service
@RequiredArgsConstructor
public class MicroserviceSubscriptionsServiceWrapper {

    private final MicroserviceSubscriptionsService microserviceSubscriptionsService;

    public void runForTenant(String tenant, Runnable runnable) {
        validateSubscription(tenant);
        microserviceSubscriptionsService.runForTenant(tenant, runnable);
    }

    public <T> T callForTenant(String tenant, Callable<T> callable) {
        validateSubscription(tenant);
        return microserviceSubscriptionsService.callForTenant(tenant, callable);
    }

    public Optional<MicroserviceCredentials> getCredentials(String tenant) {
        return microserviceSubscriptionsService.getCredentials(tenant);
    }

    private void validateSubscription(String tenant) {
        try {
            if (microserviceSubscriptionsService.getCredentials(tenant).isEmpty()) {
                log.error("There is no subscription for tenant: {}", tenant);
                throw new TenantNotSubscribedException("There is no subscription for tenant: " + tenant);
            }
        } catch (SDKException e) {
            log.error("There is no subscription for tenant: {}", tenant, e);
            throw new TenantNotSubscribedException("There is no subscription for tenant: " + tenant);
        }
    }
}
