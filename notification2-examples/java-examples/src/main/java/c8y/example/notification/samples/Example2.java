package c8y.example.notification.samples;

import c8y.example.notification.common.websocket.WebSocketClient;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionFilterRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionRepresentation;

import java.util.List;

/**
 * Multiple consumers using the same topic and each getting a copy of the notifications
 */
public class Example2 extends Notification2Example {

    private static final int SOURCE_ID = 883202;
    private static final String SUBSCRIPTION_NAME = "Example2Subscription";
    private static final String SUBSCRIBER_1_NAME = "Example2Subscriber1";
    private static final String SUBSCRIBER_2_NAME = "Example2Subscriber2";


    public static void main(String[] args) throws Exception {
        new Example2().run();
    }

    private void run() throws Exception {
        // Create subscription to listen to measurements on device with SOURCE_ID
        createSubscription(getTestNotificationSubscriptionRepresentation(SOURCE_ID));

        // Obtain authorization token
        final String subscriber1Token = createToken(SUBSCRIBER_1_NAME, SUBSCRIPTION_NAME);
        final String subscriber2Token = createToken(SUBSCRIBER_2_NAME, SUBSCRIPTION_NAME);

        // Connect to WebSocket server to receive notifications
        final WebSocketClient client1 = connectAndReceiveNotifications(subscriber1Token, SUBSCRIBER_1_NAME);
        final WebSocketClient client2 = connectAndReceiveNotifications(subscriber2Token, SUBSCRIBER_2_NAME);

        System.out.println("Press Enter to quit ...");
        System.in.read();

        // Best Practice: It's always recommended to unsubscribe a subscriber that is likely to never run again.
        unsubscribe(subscriber1Token);
        unsubscribe(subscriber2Token);

        if (client1 != null) {
            client1.close();
        }

        if (client2 != null) {
            client2.close();
        }
    }

    private NotificationSubscriptionRepresentation getTestNotificationSubscriptionRepresentation(long sourceId) {
        final ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(sourceId));

        final NotificationSubscriptionFilterRepresentation filterRepresentation = new NotificationSubscriptionFilterRepresentation();
        filterRepresentation.setApis(List.of("measurements"));

        final NotificationSubscriptionRepresentation subscriptionRepresentation = new NotificationSubscriptionRepresentation();
        subscriptionRepresentation.setContext("mo");
        subscriptionRepresentation.setSubscription(SUBSCRIPTION_NAME);
        subscriptionRepresentation.setSource(source);
        subscriptionRepresentation.setSubscriptionFilter(filterRepresentation);

        return subscriptionRepresentation;
    }
}
