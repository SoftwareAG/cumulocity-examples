package c8y.example.LongPolling;

import c8y.LoriotUplinkRequest;
import c8y.Position;
import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.customdecoders.api.util.ObjectUtils;
import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.slf4j.MDC;
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

    private static final String C8Y_SPREADING_FACTOR = "c8y_SpreadingFactor";
    private final static String REQUEST_ID = "requestId";
    public final static String EXTERNAL_ID_TYPE = "c8y_LoriotEUI";

    private EventApi eventApi;

    private InventoryApi inventoryApi;

    private IdentityApi identityApi;

    private MeasurementApi measurementApi;

    private LoriotEventFactory loriotEventFactory;

    private ContextService<MicroserviceCredentials> contextService;

    @Value("${C8Y.baseURL}")
    private String baseURL;

    @Autowired
    public CumulocityService(EventApi eventApi, InventoryApi inventoryApi, MeasurementApi measurementApi,
                             IdentityApi identityApi, LoriotEventFactory loriotEventFactory, ContextService<MicroserviceCredentials> contextService) {
        this.eventApi = eventApi;
        this.inventoryApi = inventoryApi;
        this.measurementApi = measurementApi;
        this.identityApi = identityApi;
        this.loriotEventFactory = loriotEventFactory;
        this.contextService = contextService;
    }

    public EventRepresentation createEvent(EventRepresentation event) {
        MicroserviceCredentials noAppKeyContext = createContextWithoutAppKey(contextService.getContext());
        return contextService.callWithinContext(noAppKeyContext, () -> eventApi.create(event));
    }

    public EventRepresentation createEvent(GId id, LoriotUplinkRequest data) {
        EventRepresentation eventRepresentation = loriotEventFactory.createEvent(id, data);
        MicroserviceCredentials noAppKeyContext = createContextWithoutAppKey(contextService.getContext());
        return contextService.callWithinContext(noAppKeyContext, () -> eventApi.create(eventRepresentation));
    }

    public ManagedObjectRepresentation createInventory(ManagedObjectRepresentation managedObjectRepresentation) {
        return inventoryApi.create(managedObjectRepresentation);
    }

    public void createMeasurement(MeasurementRepresentation measurement) {
        measurementApi.create(measurement);
        log.debug("Request Id {}, Created Measurement: [{}]", MDC.get(REQUEST_ID), measurement);
    }

    public void updateDevicePositionAndSpreadingFactor(ManagedObjectRepresentation deviceMo, Position position, String dataRate) {
        ManagedObjectRepresentation updateDevice = new ManagedObjectRepresentation();
        updateDevice.setId(deviceMo.getId());
        updateDevice.set(position);

        // data rate contains info about spreading factor, bandwidth and coding rate
        // e.g. SF12 BW125 4/5
        // Hence we need to split the string and take the first substring.
        if (!ObjectUtils.isEmpty(dataRate)) {
            updateDevice.setProperty(C8Y_SPREADING_FACTOR,dataRate.trim().split("\\s+")[0]);
        }

        log.debug("Request Id {}, Updating the device with managed object id {}", MDC.get(REQUEST_ID), deviceMo.getId());
        inventoryUpdate(updateDevice);

    }

    public void createPositionEvent(ManagedObjectRepresentation deviceMo, Position position, long timeStamp) {
        log.debug("Request Id {}, Creating event for position for the device with managed object id {}", MDC.get(REQUEST_ID), deviceMo.getId());
        EventRepresentation eventRepresentation = loriotEventFactory.createEventForPosition(position, new DateTime(timeStamp), deviceMo);
        createEvent(eventRepresentation);
    }


    public ManagedObjectRepresentation inventoryUpdate(ManagedObjectRepresentation inventory) {
        return inventoryApi.update(inventory);
    }

    public GId findGid(ID id) {
        return identityApi.getExternalId(id).getManagedObject().getId();
    }

    public ManagedObjectRepresentation getInventory(GId gid) {
        return inventoryApi.get(gid);
    }

    public ExternalIDRepresentation createIdentity(ExternalIDRepresentation externalID) {
        return identityApi.create(externalID);
    }

    public com.google.common.base.Optional<ExternalIDRepresentation> getExternalID(String externalID) {
        com.google.common.base.Optional<ExternalIDRepresentation> externalIDOptional = handled(
                new Callable<ExternalIDRepresentation>() {
                    @Override
                    public ExternalIDRepresentation call() throws Exception {
                        return identityApi.getExternalId(new ID(EXTERNAL_ID_TYPE, externalID));
                    }
                });
        return externalIDOptional;
    }

    public String getExternalIdByGId(GId deviceGId) {
        Optional<ExternalIDRepresentation> externalIDRepresentation = StreamSupport.stream(identityApi.getExternalIdsOfGlobalId(deviceGId).
                get(2000).spliterator(), true).filter(x -> x.getType().equals("c8y_LoriotEUI")).findFirst();
        return externalIDRepresentation.map(ExternalIDRepresentation::getExternalId).orElse(null);
    }

    public Iterable<ManagedObjectRepresentation> getInventoryMOs(InventoryFilter inventoryFilter) {
        return inventoryApi.getManagedObjectsByFilter(inventoryFilter).get(2000).allPages();
    }

    private MicroserviceCredentials createContextWithoutAppKey(MicroserviceCredentials source) {
        return new MicroserviceCredentials(
                source.getTenant(),
                source.getUsername(),
                source.getPassword(),
                source.getOAuthAccessToken(),
                source.getXsrfToken(),
                source.getTfaToken(),
                null
        );
    }
}
