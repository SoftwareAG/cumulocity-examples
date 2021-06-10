package c8y.example.notification.helloworld;

import c8y.example.notification.helloworld.platform.SubscriptionRepository;
import c8y.example.notification.helloworld.platform.TokenService;
import c8y.example.notification.helloworld.websocket.NotificationCallback;
import c8y.example.notification.helloworld.websocket.NotificationConsumerWebSocket2;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionFilterRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationTokenRequestRepresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

/**
 * TODO
 */

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class NotificationExample {

    private final static String WEBSOCKET_URL = "%s/c8y/relnotif/consumer/?token=%s";

    private final TokenService tokenService;
    private final SubscriptionRepository subscriptionRepository;
    private final Properties properties;

    @EventListener
    public void onSubscriptionAdded(MicroserviceSubscriptionAddedEvent event) {
        log.info("Subscription added for Tenant ID: <{}> ", event.getCredentials().getTenant());
        runExample();
    }

    private void runExample() {
        // Create Subscription for source device
        final String subscription = createSubscription();

        // Obtain authorization token
        final String token = createToken(subscription);

        // Connect to WebSocket server to receive notifications
        connectAndReceiveNotifications(token);
    }

    private void connectAndReceiveNotifications(String token) {
        final String webSocketUrl = getWebSocketUrl(token);

        NotificationConsumerWebSocket2 socket = new NotificationConsumerWebSocket2(new NotificationCallback() {
            @Override
            public void onNotification(List<String> headers, String notification) {
                for (String header : headers) {
                    log.info("header " + header);
                }
                log.info("notification " + notification);
            }

            @Override
            public void close() {
                log.info("close");
            }
        });

        try {
            socket.run(new URI(webSocketUrl), 60, 0);
        } catch (Exception e) {
            log.error("Error connecting to WebSocket URL", e);
        }
    }

    private String getWebSocketUrl(String token) {
        return String.format(WEBSOCKET_URL, properties.getWebSocketBaseUrl(), token);
    }

    private String createSubscription() {
        final NotificationSubscriptionRepresentation subscriptionRepresentation = getSampleSubscriptionRepresentation();

        if (!subscriptionRepository.exists(subscriptionRepresentation.getSource().getId())) {
            log.info("Subscription does not exist. Creating ...");
            subscriptionRepository.create(subscriptionRepresentation);
        }

        return subscriptionRepresentation.getSubscription();
    }

    private String createToken(String subscription) {
        final NotificationTokenRequestRepresentation tokenRequestRepresentation = new NotificationTokenRequestRepresentation(
                properties.getSubscriber(),
                subscription,
                1440,
                false);

        return tokenService.create(tokenRequestRepresentation);
    }

    private NotificationSubscriptionRepresentation getSampleSubscriptionRepresentation() {
        final ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(properties.getSourceId()));

        final String subscriptionName = "test" + source.getId().getValue() + "subscription";

        final NotificationSubscriptionFilterRepresentation filterRepresentation = new NotificationSubscriptionFilterRepresentation();
        filterRepresentation.setApis(List.of("measurements"));
        filterRepresentation.setTypeFilter("c8y_Speed");

        final NotificationSubscriptionRepresentation subscriptionRepresentation = new NotificationSubscriptionRepresentation();
        subscriptionRepresentation.setContext("mo");
        subscriptionRepresentation.setSubscription(subscriptionName);
        subscriptionRepresentation.setSource(source);
        subscriptionRepresentation.setSubscriptionFilter(filterRepresentation);
        subscriptionRepresentation.setFragmentsToCopy(List.of("c8y_SpeedMeasurement", "c8y_MaxSpeedMeasurement"));

        return subscriptionRepresentation;
    }

}
