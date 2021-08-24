package c8y.example.notification.helloworld.websocket.jetty;

import c8y.example.notification.helloworld.websocket.Notification;
import c8y.example.notification.helloworld.websocket.NotificationCallback;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.net.URI;
import java.net.URISyntaxException;

@WebSocket
@Slf4j
public class JettyWebSocketClient{

    private final NotificationCallback callback;
    private Session session = null;
    private URI serverUri;

    public JettyWebSocketClient(URI serverUri, NotificationCallback callback) {
        this.serverUri = serverUri;
        this.callback = callback;
    }

    public void connect() throws Exception {
        WebSocketClient client = new WebSocketClient();
        client.start();
        client.connect(this, this.serverUri, new ClientUpgradeRequest());
    }

    @OnWebSocketConnect
    public void onOpen(Session session) throws URISyntaxException {
        this.session = session;
        this.callback.onOpen(new URI("ws", session.getRemoteAddress().getHostName(), null));
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        Notification notification = Notification.parse(message);
        this.callback.onNotification(notification);
        if (notification.getAckHeader() != null) {
            try {
                session.getRemote().sendString(notification.getAckHeader()); // ack message
            } catch (Exception e) {
                log.error("Failed to ack message " + notification.getAckHeader(), e);
            }
        } else {
            log.warn("No message id found for ack");
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        log.info("WebSocket closed. Code:" + statusCode + ", reason: " + reason);
        this.callback.onClose();
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        log.error("WebSocket error:" + t);
        this.callback.onError(t);
    }
}
