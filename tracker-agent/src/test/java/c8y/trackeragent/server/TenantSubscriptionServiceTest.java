package c8y.trackeragent.server;

import c8y.MicroserviceSubscriptionsServiceMock;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.rest.representation.application.ApplicationReferenceCollectionRepresentation;
import com.cumulocity.rest.representation.application.ApplicationReferenceRepresentation;
import com.cumulocity.rest.representation.application.ApplicationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashSet;

class TenantSubscriptionServiceTest {

    private static final String OWNER_TENANT = "ownerTenant";
    private static final String APPLICATION_KEY = "tracker-agent";

    private final DeviceCredentialsRepository deviceCredentialsRepository = Mockito.mock(DeviceCredentialsRepository.class);
    private final SubscriptionApi subscriptionApi = Mockito.mock(SubscriptionApi.class);
    private final MicroserviceSubscriptionsService microserviceSubscriptionsService = new MicroserviceSubscriptionsServiceMock();

    private TenantSubscriptionService service;

    @BeforeEach
    void before() {
        service = new TenantSubscriptionService(
                deviceCredentialsRepository, subscriptionApi, microserviceSubscriptionsService
        );
        Mockito.when(subscriptionApi.getApplicationKey()).thenReturn(APPLICATION_KEY);
    }

    @Test
    void shouldThrowExceptionIfNotFoundApplicationInOwnerTenant() {
        //given
        ApplicationReferenceCollectionRepresentation emptyRepresentation = new ApplicationReferenceCollectionRepresentation();
        Mockito.when(subscriptionApi.getApplications(OWNER_TENANT)).thenReturn(emptyRepresentation);

        //when
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.subscribeTenants(OWNER_TENANT);
        });
    }

    @Test
    void shouldDoNothingIfThereWasNoTenantsInPropertyFile() {
        //given
        ApplicationReferenceCollectionRepresentation representations = getApplicationReferenceRepresentations();
        Mockito.when(subscriptionApi.getApplications(OWNER_TENANT)).thenReturn(representations);
        Mockito.when(deviceCredentialsRepository.getAllTenants()).thenReturn(new HashSet<>());

        //when
        service.subscribeTenants(OWNER_TENANT);

        //then
        Mockito.verify(subscriptionApi, Mockito.never())
                .subscribe(Mockito.anyString(), Mockito.any(ApplicationReferenceRepresentation.class));
    }

    @Test
    void shouldSubscribeOneTenantWithIsNotSubscribed() {
        //given
        final String tenant = "tenant1";
        ApplicationReferenceCollectionRepresentation representations = getApplicationReferenceRepresentations();
        Mockito.when(subscriptionApi.getApplications(OWNER_TENANT)).thenReturn(representations);
        ApplicationReferenceCollectionRepresentation emptyRepresentation = new ApplicationReferenceCollectionRepresentation();
        Mockito.when(subscriptionApi.getApplications(tenant)).thenReturn(emptyRepresentation);
        Mockito.when(deviceCredentialsRepository.getAllTenants()).thenReturn(Sets.newHashSet(tenant));

        //when
        service.subscribeTenants(OWNER_TENANT);

        //then
        Mockito.verify(subscriptionApi, Mockito.times(1))
                .subscribe(Mockito.eq(tenant), Mockito.any(ApplicationReferenceRepresentation.class));
    }

    @Test
    void shouldNotSubscribeTenantWithIsAlreadySubscribed() {
        //given
        final String tenant = "tenant1";
        ApplicationReferenceCollectionRepresentation representations = getApplicationReferenceRepresentations();
        Mockito.when(subscriptionApi.getApplications(OWNER_TENANT)).thenReturn(representations);
        Mockito.when(subscriptionApi.getApplications(tenant)).thenReturn(representations);
        Mockito.when(deviceCredentialsRepository.getAllTenants()).thenReturn(Sets.newHashSet(tenant));

        //when
        service.subscribeTenants(OWNER_TENANT);

        //then
        Mockito.verify(subscriptionApi, Mockito.never())
                .subscribe(Mockito.eq(tenant), Mockito.any(ApplicationReferenceRepresentation.class));
    }

    @Test
    void shouldNotSubscribeBecauseTenantFromListNotExists() {
        //given
        final String notExistingTenant = "tenant1";
        ApplicationReferenceCollectionRepresentation representations = getApplicationReferenceRepresentations();
        Mockito.when(subscriptionApi.getApplications(OWNER_TENANT)).thenReturn(representations);
        Mockito.when(subscriptionApi.getApplications(notExistingTenant)).thenThrow(new SDKException("Tenant not found"));
        Mockito.when(deviceCredentialsRepository.getAllTenants()).thenReturn(Sets.newHashSet(notExistingTenant));

        //when
        service.subscribeTenants(OWNER_TENANT);

        //then
        Mockito.verify(subscriptionApi, Mockito.never())
                .subscribe(Mockito.eq(notExistingTenant), Mockito.any(ApplicationReferenceRepresentation.class));
    }

    @Test
    void shouldSubscribeOneFromTwoTenantsBecauseOneIsAlreadySubscribed() {
        //given
        final String subscribedTenant = "tenant1";
        final String notSubscribedTenant = "tenant2";
        ApplicationReferenceCollectionRepresentation representationsWithApplication = getApplicationReferenceRepresentations();
        Mockito.when(subscriptionApi.getApplications(OWNER_TENANT)).thenReturn(representationsWithApplication);

        Mockito.when(subscriptionApi.getApplications(subscribedTenant)).thenReturn(representationsWithApplication);
        ApplicationReferenceCollectionRepresentation representationWithoutApplication = new ApplicationReferenceCollectionRepresentation();
        Mockito.when(subscriptionApi.getApplications(notSubscribedTenant)).thenReturn(representationWithoutApplication);
        Mockito.when(deviceCredentialsRepository.getAllTenants()).thenReturn(Sets.newHashSet(subscribedTenant, notSubscribedTenant));

        //when
        service.subscribeTenants(OWNER_TENANT);

        //then
        Mockito.verify(subscriptionApi, Mockito.times(1))
                .subscribe(Mockito.eq(notSubscribedTenant), Mockito.any(ApplicationReferenceRepresentation.class));
    }

    @Test
    void shouldSubscribeOneFromTwoTenantsBecauseOneTenantNotExists() {
        //given
        final String notExistingTenant = "notExisting";
        final String notSubscribedTenant = "tenant2";
        ApplicationReferenceCollectionRepresentation representationsWithApplication = getApplicationReferenceRepresentations();
        Mockito.when(subscriptionApi.getApplications(OWNER_TENANT)).thenReturn(representationsWithApplication);

        Mockito.when(subscriptionApi.getApplications(notExistingTenant)).thenThrow(new SDKException("Tenant not found"));
        ApplicationReferenceCollectionRepresentation representationWithoutApplication = new ApplicationReferenceCollectionRepresentation();
        Mockito.when(subscriptionApi.getApplications(notSubscribedTenant)).thenReturn(representationWithoutApplication);
        Mockito.when(deviceCredentialsRepository.getAllTenants()).thenReturn(Sets.newHashSet(notExistingTenant, notSubscribedTenant));

        //when
        service.subscribeTenants(OWNER_TENANT);

        //then
        Mockito.verify(subscriptionApi, Mockito.times(1))
                .subscribe(Mockito.eq(notSubscribedTenant), Mockito.any(ApplicationReferenceRepresentation.class));
    }

    private ApplicationReferenceCollectionRepresentation getApplicationReferenceRepresentations() {
        ApplicationReferenceCollectionRepresentation representations = new ApplicationReferenceCollectionRepresentation();
        ApplicationReferenceRepresentation representation = new ApplicationReferenceRepresentation();
        ApplicationRepresentation application = new ApplicationRepresentation();
        application.setKey(APPLICATION_KEY);
        representation.setApplication(application);
        representations.setReferences(Collections.singletonList(representation));
        return representations;
    }
}
