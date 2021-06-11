package c8y.example.notification.helloworld;

import c8y.example.notification.helloworld.platform.SubscriptionRepository;
import c8y.example.notification.helloworld.platform.TokenService;
import c8y.example.notification.helloworld.websocket.ExampleWebSocketClient;
import c8y.example.notification.helloworld.websocket.Notification;
import c8y.example.notification.helloworld.websocket.NotificationCallback;
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
import java.net.URISyntaxException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class NotificationExample {

    private final static String WEBSOCKET_URL = "%s/c8y/relnotif/consumer/?token=%s";

    private final TokenService tokenService;
    private final SubscriptionRepository subscriptionRepository;
    private final Properties properties;

    @EventListener
    public void onSubscriptionAdded(MicroserviceSubscriptionAddedEvent event) throws URISyntaxException {
        log.info("Subscription added for Tenant ID: <{}> ", event.getCredentials().getTenant());
        runExample();
    }

    private void runExample() throws URISyntaxException {
        // Create Subscription for source device
        final String subscription = createSubscription();

        // Obtain authorization token
        final String token = createToken(subscription);

        // Connect to WebSocket server to receive notifications
        connectAndReceiveNotifications(token);
    }

    private void connectAndReceiveNotifications(String token) throws URISyntaxException {

        final URI webSocketUri = getWebSocketUrl(token);

        final ExampleWebSocketClient client = new ExampleWebSocketClient(webSocketUri, new NotificationCallback() {

            // TODO: probably remove (only needed for auto reconnect behaviour in onClose())
            final long initialBackoffMillis = 1000;
            final long maxBackoffMillis = 60 * 1000;
            long backoffMillis = initialBackoffMillis;

            @Override
            public void onOpen(URI uri) {
                this.backoffMillis = this.initialBackoffMillis;
                log.info("Connected to WebSocket server " + uri);
            }

            @Override
            public void onNotification(Notification notification) {
                // NOTE: my experiences using grep over huge (multi GiB) log files
                //       has taught me that multi-line toString() is evil
                //       (hence the one-line toString() or multiline alternative here).
                log.info("Notification received:\n" + notification.toPrintString());
                //log.info("Notification received:" + notification.toString());
            }

            @Override
            public void onError(Exception e) {
                log.error("We got an exception: " + e);
            }

            @Override
            public void onClose() {
                log.info("Connection was closed.");

                //TODO: if we do this we can never close a connection (we need another
                //      shouldBeClosed boolean to control it, somewhere)
                // NOTE: we can't reconnect in a callback thread (in a method such as this) as
                //       the underlying library does not allow it (it errors) -> hence the spawned thread
                //       This makes the example untidy.
                // NOTE2: had to declare client as instance field member in order to access here
                //       (as at time of creation, client is not, or only partially, instantiated).
//                log.info("Connection was closed. Reconnecting ...");
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(Math.min(backoffMillis, maxBackoffMillis));
//                            backoffMillis *= 2;
//                            NotificationExample.this.client.reconnectBlocking();
//                        } catch (InterruptedException ignore) {
//                        }
//                    }
//                }, "ReconnectWebSocketThread").start();
            }
        });
        // TODO: consider making client an anonymous reference (if not reconnecting)
        client.connect();
    }

    private URI getWebSocketUrl(String token) throws URISyntaxException {
        return new URI(String.format(WEBSOCKET_URL, properties.getWebSocketBaseUrl(), token));
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
