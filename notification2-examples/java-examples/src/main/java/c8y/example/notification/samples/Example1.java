package c8y.example.notification.samples;

import c8y.example.notification.client.websocket.WebSocketClient;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionFilterRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionRepresentation;

import java.util.List;

/**
 * Multiple “subscriptions” using the same topic (i.e., same subscription name)
 */
public class Example1 {

    private static final int SOURCE_ID_1 = 883202;
    private static final int SOURCE_ID_2 = 663208;
    private static final String SUBSCRIBER_NAME = "Example1Subscriber";
    private static final String SUBSCRIPTION_NAME = "Example1Subscription";

    private final Notification2Example notification2Example = new Notification2Example();

    public static void main(String[] args) throws Exception {
        new Example1().run();
    }

    private void run() throws Exception {
        // Create subscription to listen to measurements on device with SOURCE_ID_1
        createNotificationSubscription(SOURCE_ID_1);

        // Create subscription to listen to measurements on device with SOURCE_ID_2
        createNotificationSubscription(SOURCE_ID_2);

        // Obtain authorization token to listen to both the subscriptions with name SUBSCRIPTION_NAME
        final String token = notification2Example.createToken(SUBSCRIBER_NAME, SUBSCRIPTION_NAME);

        // Connect to WebSocket server to receive notifications
        final WebSocketClient client = notification2Example.connectAndReceiveNotifications(token, SUBSCRIBER_NAME);

        System.out.println("Press Enter to quit ...");
        System.in.read();

        /*
         * Best Practice: It's always recommended to unsubscribe a subscriber that is likely to never run again as
         *                they can place significant storage resource demands on a system.
         */
        notification2Example.unsubscribe(token);

        if (client != null) {
            client.close();
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
