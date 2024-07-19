package c8y.example.LongPolling;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.StreamSupport;

import static c8y.example.exceptions.ExceptionHandler.handled;


@Slf4j
@Component
public class CumulocityService {

    public final static String EXTERNAL_ID_TYPE = "c8y_LoriotEUI";

    private InventoryApi inventoryApi;

    private IdentityApi identityApi;

    private ContextService<MicroserviceCredentials> contextService;

    @Value("${C8Y.baseURL}")
    private String baseURL;

    @Autowired
    public CumulocityService(InventoryApi inventoryApi, IdentityApi identityApi, ContextService<MicroserviceCredentials> contextService) {
        this.inventoryApi = inventoryApi;
        this.identityApi = identityApi;
        this.contextService = contextService;
    }


    public String getExternalIdByGId(GId deviceGId) {
        Optional<ExternalIDRepresentation> externalIDRepresentation = StreamSupport.stream(identityApi.getExternalIdsOfGlobalId(deviceGId).
                get(2000).spliterator(), true).filter(x -> x.getType().equals("c8y_LoriotEUI")).findFirst();
        return externalIDRepresentation.map(ExternalIDRepresentation::getExternalId).orElse(null);
    }

    public Iterable<ManagedObjectRepresentation> getInventoryMOs(InventoryFilter inventoryFilter) {
        return inventoryApi.getManagedObjectsByFilter(inventoryFilter).get(2000).allPages();
    }

}
