package c8y.trackeragent.devicemapping;

import c8y.MicroserviceSubscriptionsServiceMock;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceTenantMappingServiceTest {

    private static final String TYPE = "c8y_device_tenant_Imei";
    private static final String TENANT_FIELD = "c8y_tenantId";

    private final static String OWNER_TENANT = "ownerTenant";

    private final IdentityApi identityApi = Mockito.mock(IdentityApi.class);
    private final InventoryApi inventoryApi = Mockito.mock(InventoryApi.class);

    private final DeviceTenantMappingRepository deviceTenantMappingRepository = new DeviceTenantMappingRepository(
            identityApi, inventoryApi
    );

    private final MicroserviceSubscriptionsService microserviceSubscriptionsService = new MicroserviceSubscriptionsServiceMock();

    private DeviceTenantMappingService deviceTenantMappingService = new DeviceTenantMappingService(
            deviceTenantMappingRepository, microserviceSubscriptionsService, OWNER_TENANT
    );

    //findTenantOptional
    //found correctly - identityApi.getExternalId, inventoryApi.get
    //found inventoryApi throw exception
    @Test
    void shouldFindTenantCorrectly() {
        //given
        final String imei = "imei_1";
        final String tenant = "myTenant";
        ID tenantObjectId = new ID(TYPE, imei);
        ExternalIDRepresentation externalIDRepresentation = createExternalIDRepresentation(new GId(TYPE, imei, null));
        Mockito.when(identityApi.getExternalId(tenantObjectId)).thenReturn(externalIDRepresentation);
        ManagedObjectRepresentation tenantRepresentation = new ManagedObjectRepresentation();
        tenantRepresentation.set(tenant, TENANT_FIELD);
        Mockito.when(inventoryApi.get(new GId(TYPE, imei, null))).thenReturn(tenantRepresentation);

        //when
        String resultTenant = deviceTenantMappingService.findTenant(imei);

        //then
        assertThat(resultTenant).isEqualTo(tenant);
    }

    //found identityApi throw exception
//    @Test
//    void should

    //addDeviceToTenant
    //create tenant and added external id
    //create added external id for existing tenant
    //already existing assign to the same thenat
    //already existing assign to other tenant

    private ExternalIDRepresentation createExternalIDRepresentation(GId tenantObjectId) {
        ExternalIDRepresentation externalIDRepresentation = new ExternalIDRepresentation();
        ManagedObjectRepresentation managedObject = new ManagedObjectRepresentation();
        managedObject.setId(tenantObjectId);
        externalIDRepresentation.setManagedObject(managedObject);
        return externalIDRepresentation;
    }
}