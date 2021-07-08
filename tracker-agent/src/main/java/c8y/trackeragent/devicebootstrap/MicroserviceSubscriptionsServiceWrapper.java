package c8y.trackeragent.devicebootstrap;

import c8y.trackeragent.exception.TenantNotSubscribedException;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.Callable;

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
        if (microserviceSubscriptionsService.getCredentials(tenant).isEmpty()) {
            throw new TenantNotSubscribedException("There is no subscription for tenant: " + tenant);
        }
    }
}
