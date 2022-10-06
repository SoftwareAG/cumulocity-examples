package c8y.example.notification.helloworld;

import c8y.example.notification.helloworld.platform.SubscriptionRepository;
import c8y.example.notification.helloworld.platform.TokenService;
import c8y.example.notification.helloworld.websocket.Notification;
import c8y.example.notification.helloworld.websocket.NotificationCallback;
import c8y.example.notification.helloworld.websocket.jetty.JettyWebSocketClient;
import c8y.example.notification.helloworld.websocket.tootallnate.TooTallNateWebSocketClient;
import com.cumulocity.microservice.settings.service.MicroserviceSettingsService;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
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

    private void runExample() throws Exception {
        // Create Subscription for source device
        final NotificationSubscriptionRepresentation subscriptionRepresentation = createSubscription();

        // Obtain authorization token
        final String token = createToken(subscriptionRepresentation.getSubscription());

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

        final String webSocketLibrary = properties.getWebSocketLibrary();
        if (webSocketLibrary != null && webSocketLibrary.equalsIgnoreCase("jetty")) {
            log.info("WebSocket library: Jetty");
            final JettyWebSocketClient client = new JettyWebSocketClient(webSocketUri, callback);
            client.connect();
        } else {
            log.info("WebSocket library: TooTallNate");
            final TooTallNateWebSocketClient client = new TooTallNateWebSocketClient(webSocketUri, callback);
            client.connect();
        }
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

    @Data
    @Builder
    private static class TenantProperties {
        private String sourceId;
    }
}
