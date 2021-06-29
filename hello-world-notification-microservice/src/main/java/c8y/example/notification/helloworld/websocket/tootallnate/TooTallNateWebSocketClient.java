package c8y.example.notification.helloworld.websocket.tootallnate;

import c8y.example.notification.helloworld.websocket.Notification;
import c8y.example.notification.helloworld.websocket.NotificationCallback;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

@Slf4j
public class TooTallNateWebSocketClient extends WebSocketClient {

    private final NotificationCallback callback;

    public TooTallNateWebSocketClient(URI serverUri, NotificationCallback callback) {
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
