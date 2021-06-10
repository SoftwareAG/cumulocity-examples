package c8y.example.notification.helloworld.websocket;

import java.net.URI;

/**
 * Implement this interface to handle notifications.
 */
public interface NotificationCallback {

    void onOpen(URI address);

    void onError(Exception e);

    /**
     * Called on receiving a notification. The notification will be acknowledged if no exception raised.
     * @param notification the notification received.
     */
    void onNotification(Notification notification);

    /**
     * Called on close of the underlying web socket connection. Normally, a reconnection should be attempted.
     */
    void onClose();
}
