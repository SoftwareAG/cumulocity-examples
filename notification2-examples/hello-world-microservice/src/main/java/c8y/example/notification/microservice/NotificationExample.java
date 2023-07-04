package c8y.example.notification.microservice;

import c8y.example.notification.common.websocket.Notification;
import c8y.example.notification.common.websocket.NotificationCallback;
import c8y.example.notification.common.websocket.WebSocketClient;
import c8y.example.notification.common.websocket.tootallnate.TooTallNateWebSocketClient;
import c8y.example.notification.common.platform.SubscriptionRepository;
import c8y.example.notification.common.platform.TokenService;
import com.cumulocity.microservice.settings.service.MicroserviceSettingsService;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionRemovedEvent;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionFilterRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationTokenRequestRepresentation;
import com.cumulocity.sdk.client.messaging.notifications.NotificationSubscriptionCollection;
import com.cumulocity.sdk.client.messaging.notifications.NotificationSubscriptionFilter;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class NotificationExample {

    private static final String SOURCE_ID = "example.source.id";
    private final static String WEBSOCKET_URL_PATTERN = "%s/notification2/consumer/?token=%s";

    private final MicroserviceSubscriptionsService contextService;
    private final TokenService tokenService;
    private final SubscriptionRepository subscriptionRepository;
    private final Properties properties;
    private String token;
    private WebSocketClient client;

    @Autowired(required = false)
    private MicroserviceSettingsService microserviceSettingsService;

    @EventListener
    public void onSubscriptionAdded(MicroserviceSubscriptionAddedEvent event) throws Exception {
        final String tenantId = event.getCredentials().getTenant();
        log.info("Subscription added for Tenant ID: <{}> ", tenantId);

        // Override properties obtained from file with the ones obtained from tenant properties
        overrideProperties(tenantId);

        runExample();
    }

    @EventListener
    public void onSubscriptionRemoved(MicroserviceSubscriptionRemovedEvent event) throws Exception {
        // Best Practice: It's always recommended to unsubscribe a subscriber that is likely to never run again.
        unsubscribe();

        close();
    }

    private void runExample() throws Exception {
        // Create Subscription for source device
        final NotificationSubscriptionRepresentation subscriptionRepresentation = createSubscription();

        // Obtain authorization token
        token = createToken(subscriptionRepresentation.getSubscription());

        // Connect to WebSocket server to receive notifications
        connectAndReceiveNotifications(token);
    }

    private void connectAndReceiveNotifications(String token) throws Exception {

        final URI webSocketUri = getWebSocketUrl(token);

        final NotificationCallback callback = new NotificationCallback() {

            @Override
            public void onOpen(URI uri) {
                log.info("Connected to Cumulocity notification service over WebSocket " + uri);
            }

            @Override
            public void onNotification(Notification notification) {
                log.info("Notification received: <{}>", notification.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                log.error("We got an exception: " + t);
            }

            @Override
            public void onClose() {
                log.info("Connection was closed.");
            }
        };

        log.info("Connecting WebSocket client ...");
        client = new TooTallNateWebSocketClient(webSocketUri, callback);
        client.connect();
    }

    private URI getWebSocketUrl(String token) throws URISyntaxException {
        return new URI(String.format(WEBSOCKET_URL_PATTERN, properties.getWebSocketBaseUrl(), token));
    }

    private NotificationSubscriptionRepresentation createSubscription() {
        final GId sourceId = GId.asGId(properties.getSourceId());
        final String subscriptionName = "test" + sourceId.getValue() + "subscription";

        final NotificationSubscriptionCollection notificationSubscriptionCollection = subscriptionRepository
                .getByFilter(new NotificationSubscriptionFilter().bySource(sourceId));
        final List<NotificationSubscriptionRepresentation> subscriptions = notificationSubscriptionCollection.get().getSubscriptions();

        final Optional<NotificationSubscriptionRepresentation> subscriptionRepresentation = subscriptions.stream()
                .filter(subscription -> subscription.getSubscription().equals(subscriptionName))
                .findFirst();

        if (subscriptionRepresentation.isPresent()) {
            log.info("Reusing existing subscription <{}> on device <{}>", subscriptionName, sourceId.getValue());
            return subscriptionRepresentation.get();
        }

        log.info("Subscription does not exist. Creating ...");
        return subscriptionRepository.create(getSampleSubscriptionRepresentation(subscriptionName));
    }

    private String createToken(String subscription) {
        final NotificationTokenRequestRepresentation tokenRequestRepresentation = new NotificationTokenRequestRepresentation(
                properties.getSubscriber(),
                subscription,
                1440,
                false);

        return tokenService.create(tokenRequestRepresentation);
    }

    private NotificationSubscriptionRepresentation getSampleSubscriptionRepresentation(String subscriptionName) {
        final ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(properties.getSourceId()));

        final NotificationSubscriptionFilterRepresentation filterRepresentation = new NotificationSubscriptionFilterRepresentation();
        filterRepresentation.setApis(List.of("measurements"));
        filterRepresentation.setTypeFilter("'c8y_Speed'");

        final NotificationSubscriptionRepresentation subscriptionRepresentation = new NotificationSubscriptionRepresentation();
        subscriptionRepresentation.setContext("mo");
        subscriptionRepresentation.setSubscription(subscriptionName);
        subscriptionRepresentation.setSource(source);
        subscriptionRepresentation.setSubscriptionFilter(filterRepresentation);
        subscriptionRepresentation.setFragmentsToCopy(List.of("c8y_SpeedMeasurement", "c8y_MaxSpeedMeasurement"));

        return subscriptionRepresentation;
    }

    private void overrideProperties(String tenantId) {
        final Optional<TenantProperties> tenantPropertiesOptional = contextService.callForTenant(tenantId, this::getTenantProperties);

        if (tenantPropertiesOptional.isEmpty()) {
            return;
        }

        final TenantProperties tenantProperties = tenantPropertiesOptional.get();
        log.info("Loaded tenant properties: <{}>", tenantProperties.toString());

        if (tenantProperties.getSourceId() != null) {
            properties.setSourceId(tenantProperties.getSourceId());
        }
    }

    private Optional<TenantProperties> getTenantProperties() {
        if (microserviceSettingsService == null || microserviceSettingsService.getAll().isEmpty()) {
            return Optional.empty();
        }

        final String sourceId = microserviceSettingsService.get(SOURCE_ID);

        return Optional.of(TenantProperties.builder()
                .sourceId(sourceId)
                .build());
    }

    private void unsubscribe() {
        if (token != null) {
            // To unsubscribe, you can pass any token to the unsubscribe API. It does not have to be the one first obtained. You can generate a fresh token
            // if needed with the matching subscriber and subscription fields and pass that along to unsubscribe API.
            tokenService.unsubscribe(token);
        }
    }

    private void close() throws Exception {
        if (client != null) {
            client.close();
        }
    }

    @Data
    @Builder
    private static class TenantProperties {
        private String sourceId;
    }
}
