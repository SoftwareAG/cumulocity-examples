package c8y.example.notification.helloworld.websocket;

import java.util.List;

/**
 * Implement this interface to handle notifications.
 */
public interface NotificationCallback {
    /**
     * Called on receiving a notification. The notification will be acknowledged if no exception raised.
     * @param headers the headers describing the notification.
     * @param notification the notification as a JSON string.
     */
    void onNotification(List<String> headers, String notification);

    /**
     * Called on close of the underlying web socket connection. Normally, a reconnection should be attempted.
     *
     * TODO: should we include websocket close-code and reason parameters
     * TODO: We should NOT according to https://datatracker.ietf.org/doc/html/rfc6455#section-5.5.1 (Section 5.5.1 Close, end of first paragraph)
     */
    void close();
}
