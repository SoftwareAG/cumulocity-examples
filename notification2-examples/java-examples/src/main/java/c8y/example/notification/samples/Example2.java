package c8y.example.notification.samples;

import c8y.example.notification.client.websocket.WebSocketClient;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionFilterRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionRepresentation;

import java.util.List;

/**
 * Multiple consumers using the same topic and each getting a copy of the notifications
 */
public class Example2 {

    private static final int SOURCE_ID = 883202;
    private static final String SUBSCRIPTION_NAME = "Example2Subscription";
    private static final String SUBSCRIBER_1_NAME = "Example2Subscriber1";
    private static final String SUBSCRIBER_2_NAME = "Example2Subscriber2";

    private final Notification2Example notification2Example = new Notification2Example();

    public static void main(String[] args) throws Exception {
        new Example2().run();
    }

    private void run() throws Exception {
        // Create subscription to listen to measurements on device with SOURCE_ID
        createNotificationSubscription(SOURCE_ID);

        // Obtain authorization token
        final String subscriber1Token = notification2Example.createToken(SUBSCRIBER_1_NAME, SUBSCRIPTION_NAME);
        final String subscriber2Token = notification2Example.createToken(SUBSCRIBER_2_NAME, SUBSCRIPTION_NAME);

        // Connect to WebSocket server to receive notifications
        final WebSocketClient client1 = notification2Example.connectAndReceiveNotifications(subscriber1Token, SUBSCRIBER_1_NAME);
        final WebSocketClient client2 = notification2Example.connectAndReceiveNotifications(subscriber2Token, SUBSCRIBER_2_NAME);

        System.out.println("Press Enter to quit ...");
        System.in.read();

        /*
         * Best Practice: It's always recommended to unsubscribe a subscriber that is likely to never run again as
         *                they can place significant storage resource demands on a system.
         */
        notification2Example.unsubscribe(subscriber1Token);
        notification2Example.unsubscribe(subscriber2Token);

        if (client1 != null) {
            client1.close();
        }

        if (client2 != null) {
            client2.close();
        }
    }

    private void createNotificationSubscription(long sourceId) {
        final ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(sourceId));

        final NotificationSubscriptionFilterRepresentation filterRepresentation = new NotificationSubscriptionFilterRepresentation();
        filterRepresentation.setApis(List.of("measurements"));

        final NotificationSubscriptionRepresentation subscriptionRepresentation = new NotificationSubscriptionRepresentation();
        subscriptionRepresentation.setContext("mo");
        subscriptionRepresentation.setSubscription(SUBSCRIPTION_NAME);
        subscriptionRepresentation.setSource(source);
        subscriptionRepresentation.setSubscriptionFilter(filterRepresentation);

        notification2Example.createSubscription(subscriptionRepresentation);
    }
}
