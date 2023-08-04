package c8y.example.notification.client.websocket.tootallnate;

import c8y.example.notification.client.websocket.Notification;
import c8y.example.notification.client.websocket.NotificationCallback;
import c8y.example.notification.client.websocket.WebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

@Slf4j
public class TooTallNateWebSocketClient implements WebSocketClient {

    private org.java_websocket.client.WebSocketClient client;

    public TooTallNateWebSocketClient(URI serverUri, NotificationCallback callback) {
        super();
        this.client = new WebSocketClientImpl(serverUri, callback);
    }

    @Override
    public void connect() throws InterruptedException {
        this.client.connectBlocking();
    }

    @Override
    public void close() throws InterruptedException {
        this.client.closeBlocking();
    }

    private static class WebSocketClientImpl extends org.java_websocket.client.WebSocketClient {
        private final NotificationCallback callback;

        WebSocketClientImpl(URI serverUri, NotificationCallback callback) {
            super(serverUri);
            this.callback = callback;
        }

        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            this.callback.onOpen(this.uri);
        }

        @Override
        public void onMessage(String message) {
            Notification notification = Notification.parse(message);
            this.callback.onNotification(notification);
            if (notification.getAckHeader() != null) {
                // Best Practice: Acknowledge notifications as soon as possible.
                send(notification.getAckHeader()); // ack message
            } else {
                throw new RuntimeException("No message id found for ack");
            }
        }

        @Override
        public void onClose(int statusCode, String reason, boolean remote) {
            log.info("WebSocket closed " + (remote ? "by server. " : "") + " Code:" + statusCode + ", reason: " + reason);
            this.callback.onClose();
        }

        @Override
        public void onError(Exception e) {
            log.error("WebSocket error:" + e);
            this.callback.onError(e);
        }
    }
}
