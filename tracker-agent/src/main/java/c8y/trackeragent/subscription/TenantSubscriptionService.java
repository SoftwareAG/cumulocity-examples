package c8y.trackeragent.subscription;

import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.devicebootstrap.MicroserviceSubscriptionsServiceWrapper;
import c8y.trackeragent.exception.TenantNotSubscribedException;
import com.cumulocity.rest.representation.application.ApplicationReferenceCollectionRepresentation;
import com.cumulocity.rest.representation.application.ApplicationReferenceRepresentation;
import com.cumulocity.sdk.client.SDKException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TenantSubscriptionService {

    private final DeviceCredentialsRepository deviceCredentialsRepository;
    private final SubscriptionApi subscriptionApi;
    private final MicroserviceSubscriptionsServiceWrapper microserviceSubscriptionsServiceWrapper;


    public void subscribeTenants(String ownerTenant) {
        Set<String> allTenants = deviceCredentialsRepository.getAllTenants();
        microserviceSubscriptionsServiceWrapper.runForTenant(ownerTenant, () -> {
            subscribeAll(ownerTenant, allTenants);
        });
    }

    private void subscribeAll(String ownerTenant, Set<String> tenantsToSubscribe) {
        Optional<ApplicationReferenceRepresentation> trackerAgentApplication = getTrackerAgentApplication(ownerTenant);
        if (trackerAgentApplication.isEmpty()) {
            throw new TenantNotSubscribedException("Not found application in owner tenant: " + ownerTenant);
        }
        for (String tenant : tenantsToSubscribe) {
            subscribeIfNeeded(tenant, trackerAgentApplication.get());
        }
    }

    private void subscribeIfNeeded(String tenant, ApplicationReferenceRepresentation application) {
        if (isNeedToSubscribe(tenant)) {
            log.info("Try to subscribe tracker-agent to tenant: {}", tenant);
            subscriptionApi.subscribe(tenant, application);
            log.info("Tracker-agent for tenant {} subscribed", tenant);
        }
    }

    private Optional<ApplicationReferenceRepresentation> getTrackerAgentApplication(String tenant) {
        ApplicationReferenceCollectionRepresentation representation;
        try {
            representation = subscriptionApi.getApplications(tenant);
        } catch (SDKException e) {
            logException(tenant, e);
            return Optional.empty();
        }
        return filterTrackerAgentApplication(representation);
    }

    private boolean isNeedToSubscribe(String tenant) {
        ApplicationReferenceCollectionRepresentation representation;
        try {
            representation = subscriptionApi.getApplications(tenant);
        } catch (SDKException e) {
            logException(tenant, e);
            return false;
        }
        return filterTrackerAgentApplication(representation).isEmpty();
    }

    private Optional<ApplicationReferenceRepresentation> filterTrackerAgentApplication(ApplicationReferenceCollectionRepresentation representation) {
        return representation.getReferences().stream()
                .filter(a -> a.getApplication().getKey().equals(subscriptionApi.getApplicationKey()))
                .findAny();
    }

    private void logException(String tenant, SDKException e) {
        log.error("Not able to load applications for tenant: {}", tenant);
        log.error("Exception: {}, details: {}",
                e.getClass(),
                StringUtils.splitByWholeSeparator(e.getMessage(), "\n\t")[0]);
    }
}
