package c8y.example.LongPolling;

import c8y.IsDevice;
import com.cumulocity.lpwan.lns.connection.model.LpwanDeviceFilter;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@AllArgsConstructor
public class LongPollingService {
    private static final String LORIOT_PROVIDER = "Loriot";

    private final MicroserviceSubscriptionsService microserviceSubscriptionsService;
    private final CumulocityService cumulocityService;
    private final DeviceControlApi deviceControlApi;

    @Getter
    private final AtomicInteger deviceCounter;

    @EventListener(MicroserviceSubscriptionAddedEvent.class)
    public void onSubscriptionEvent(MicroserviceSubscriptionAddedEvent event) {
        MicroserviceCredentials credentials = event.getCredentials();
        microserviceSubscriptionsService.runForTenant(credentials.getTenant(), new Runnable() {

            @Override
            public void run() {
                try {
                    LpwanDeviceFilter loriotDeviceFilter = LpwanDeviceFilter.byServiceProvider(LORIOT_PROVIDER);
                    cumulocityService.getInventoryMOs(loriotDeviceFilter.
                            byFragmentType(IsDevice.class))
                            .spliterator().forEachRemaining(managedObjectRepresentation -> {
                                log.info("Subscribing to device {}", managedObjectRepresentation.getId().getValue());
                        String devEui = cumulocityService.getExternalIdByGId(managedObjectRepresentation.getId());
                        try {
                            deviceCounter.getAndIncrement();
                            subscribeToOperationListener(managedObjectRepresentation.getId(), credentials.getTenant(), devEui);
                        } catch (Exception ex) {
                            log.error("Subscription error for device {}", managedObjectRepresentation.getId());
                        }
                    });
                } catch (Exception e) {
                    log.error("Error occurred while subscribing devices of the tenant {} to the operation subscription channel. {}", credentials.getTenant(), e.getMessage());
                }
            }
        });

    }

    public void subscribeToOperationListener(GId gId, String tenant, String devEui) {
        OperationSubscriptionListener operationSubscriptionListener = new OperationSubscriptionListener(gId, tenant, devEui);
        deviceControlApi.getNotificationsSubscriber().subscribe(gId, operationSubscriptionListener);
    }

    public class OperationSubscriptionListener implements SubscriptionListener<GId, OperationRepresentation> {

        String tenant;
        GId gId;
        String devEui;

        OperationSubscriptionListener(GId managedObjectRepresentation, String tenant, String deveui) {
            this.tenant = tenant;
            this.gId = managedObjectRepresentation;
            this.devEui = deveui;
        }

        @Override
        public void onNotification(Subscription<GId> subscription, OperationRepresentation operationRepresentation) {
            log.info("Operation processed successfully");
        }

        @Override
        public void onError(Subscription<GId> subscription, Throwable ex) {
            log.warn("Notification subscriber received error", ex);
        }
    }
}
