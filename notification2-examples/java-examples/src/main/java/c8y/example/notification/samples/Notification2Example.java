package c8y.example.notification.samples;

import c8y.example.notification.common.websocket.Notification;
import c8y.example.notification.common.websocket.NotificationCallback;
import c8y.example.notification.common.websocket.WebSocketClient;
import c8y.example.notification.common.websocket.tootallnate.TooTallNateWebSocketClient;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationTokenRequestRepresentation;
import com.cumulocity.sdk.client.messaging.notifications.NotificationSubscriptionCollection;
import com.cumulocity.sdk.client.messaging.notifications.NotificationSubscriptionFilter;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class Notification2Example {
    final static String WEBSOCKET_URL_PATTERN = "%s/notification2/consumer/?token=%s";
    final PlatformConfiguration platformConfiguration = PlatformConfiguration.getPlatformConfiguration();

    void createSubscription(NotificationSubscriptionRepresentation subscriptionRepresentation) {
        final NotificationSubscriptionCollection notificationSubscriptionCollection = platformConfiguration
                .getSubscriptionRepository()
                .getByFilter(new NotificationSubscriptionFilter().bySource(subscriptionRepresentation.getSource().getId()));
        final List<NotificationSubscriptionRepresentation> subscriptions = notificationSubscriptionCollection.get().getSubscriptions();

        final Optional<NotificationSubscriptionRepresentation> optionalNotificationSubscriptionRepresentation = subscriptions.stream()
                .filter(subscription -> subscription.getSubscription().equals(subscriptionRepresentation.getSubscription()))
                .findFirst();

        if (optionalNotificationSubscriptionRepresentation.isPresent()) {
            log.info("Reusing existing subscription <{}> on device <{}>", subscriptionRepresentation.getSubscription(), subscriptionRepresentation.getSource().getId());
            return;
        }

        log.info("Subscription does not exist. Creating ...");
        platformConfiguration.getSubscriptionRepository().create(subscriptionRepresentation);
    }

    String createToken(String subscriber, String subscription) {
        final NotificationTokenRequestRepresentation tokenRequestRepresentation = new NotificationTokenRequestRepresentation(
                subscriber,
                subscription,
                1440,
                false);

        return platformConfiguration.getTokenService().create(tokenRequestRepresentation);
    }

    WebSocketClient connectAndReceiveNotifications(String token, String subscriber) throws Exception {

        final URI webSocketUri = getWebSocketUrl(token);

        final NotificationCallback callback = new NotificationCallback() {

            @Override
            public void onOpen(URI uri) {
                log.info("[Subscriber = {}] Connected to Cumulocity notification service over WebSocket {}", subscriber, uri);
            }

            @Override
            public void onNotification(Notification notification) {
                System.out.println(notification.getMessage());
                log.info("[Subscriber = {}] Notification received: <{}>", subscriber, notification.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                log.error("[Subscriber = {}] We got an exception: " + t, subscriber);
            }

            @Override
            public void onClose() {
                log.info("[Subscriber = {}] Connection was closed.", subscriber);
            }
        };

        log.info("[Subscriber = {}] Connecting WebSocket client ...", subscriber);
        final WebSocketClient client = new TooTallNateWebSocketClient(webSocketUri, callback);
        client.connect();
        return client;
    }

    void unsubscribe(String token) {
        log.info("Unsubscribing ...");
        if (token != null) {
            // To unsubscribe, you can pass any token to the unsubscribe API. It does not have to be the one first obtained. You can generate a fresh token
            // if needed with the matching subscriber and subscription fields and pass that along to unsubscribe API.
            platformConfiguration.getTokenService().unsubscribe(token);
        }
    }

    private URI getWebSocketUrl(String token) throws URISyntaxException {
        return new URI(String.format(WEBSOCKET_URL_PATTERN, platformConfiguration.getWebsocketUrl(), token));
    }

}
