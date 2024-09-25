package c8y.example.notification.client.websocket.tootallnate;

import c8y.example.notification.client.websocket.Notification;
import c8y.example.notification.client.websocket.NotificationCallback;
import c8y.example.notification.client.websocket.WebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocketImpl;
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
            try {
                //Recommended workaround for client issue that can start disconnection if the PONG response are delayed because of busy socket (the RFC 6455 specification does not require the client to send Ping frames if it is constantly receiving messages from the server).
                //((WebSocketImpl) getConnection()).updateLastPong();
                Notification notification = Notification.parse(message);
                callback.onNotification(notification);
                acknowledge(notification);
            } catch (Throwable e) {
                log.error("Error processing message '{}'. Acknowledge will not be sent so the message will be resent in the future.", message);
            }
        }

        private void acknowledge(final Notification notification) {
            if (notification.getAckHeader() != null) {
                // Best Practice: Acknowledge notifications as soon as possible.
                send(notification.getAckHeader()); // ack message
            } else {
                log.error("Invalid message without ACK header, message will not be acknowledged");
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
