package c8y.example.notification.helloworld.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@WebSocket
public class NotificationConsumerWebSocket {

    private Session session = null;
    private Object muLock = new Object();
    private boolean failed, closed, busy;

    private final NotificationCallback callback;

    public NotificationConsumerWebSocket(NotificationCallback callback) {
        this.callback = callback;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
    }

    private class ParseResult {
        String ackHeader;
        List<String> notificationHeaders;
        String message;
    }

    private ParseResult parseHeaders(String message) {
        ParseResult results = new ParseResult();
        ArrayList<String> headers = new ArrayList<>(8);
        while(true) {
            int i = message.indexOf('\n');
            if (i == -1) {
                break;
            }
            String header = message.substring(0, i);
            message = message.substring(i + 1);
            if (header.length() == 0) {
                break;
            }
            headers.add(header);
        }
        results.message = message;
        if (headers.isEmpty()) {
            return results;
        }
        results.ackHeader = headers.get(0);
        results.notificationHeaders = headers.subList(1, headers.size());

        return results;
    }

    /**
     * Process a notification message. The message consists of one or more header lines terminated by '\n';
     * followed by an empty line (i.e. a second following '\n');
     * followed by the notification as a json text (including newline).
     * The first header is an opaque message identifier which must be returned to acknowledge the notification.
     * Additional headers depend on notification context but should consist of at least: a
     * '/' separated encoding of the type and source for the notification e.g. '/tenantId/measurements/sourceObjectId';
     * followed by an action (CREATE, UPDATE, DELETE).
     * Please see the Cumulocity API documentation for further information.
     * The configured callback is invoked with headers and notification as arguments
     * on successful parsing of the message.
     * @param message the textual message consisting of one or more headers and a notification.
     */
    @OnWebSocketMessage
    public void onMessage(String message) {

        synchronized (muLock) {
            busy = true;
        }

        ParseResult parseResult = parseHeaders(message);

        callback.onNotification(parseResult.notificationHeaders, parseResult.message);

        try {
            session.getRemote().sendString(parseResult.ackHeader); // ack message
        } catch (Exception e) {
            if (e instanceof IOException) {
                System.err.println("Failed to send ack");
            } else if (parseResult.ackHeader == null){
                System.err.println("No message id found for ack");
            } else {
                System.err.println("Failed to ack: " + e);
            }
            synchronized (muLock) {
                failed = closed = true;
                muLock.notifyAll();
            }
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("WebSocket closed. Code:" + statusCode);
        synchronized (muLock) {
            closed = true;
            muLock.notifyAll();
        }
    }

    @OnWebSocketError
    public void onError(Throwable error) {
        System.out.println("WebSocket error:" + error);
        synchronized (muLock) {
            closed = true;
            muLock.notifyAll();
        }
    }

    public boolean awaitClose(int waitInSeconds)
            throws InterruptedException {
        try {
            while (true) {
                synchronized (muLock) {
                    if (closed)
                        return failed;
                    busy = false;
                    if (waitInSeconds > 0) {
                        muLock.wait(waitInSeconds * 1000);
                    } else {
                        muLock.wait();
                    }
                    if (!closed && !busy) {
                        session.close(StatusCode.NORMAL, "idle");
                        return failed;
                    }
                }
            }
        } finally {
            callback.close();
        }
    }

    public void run(URI destUri, int maxIdle, int maxRetries) throws IOException, InterruptedException {

        WebSocketClient client = new WebSocketClient();

        try { client.start(); } catch (Exception e) { throw new IOException("Error starting client", e); }

        boolean forever = maxRetries < 0;
        while (forever || maxRetries-- >= 0) {
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            System.out.println("Connecting to: " + destUri);
            client.connect(this, destUri, request);
            this.awaitClose(maxIdle);
        }

        try { client.stop(); } catch (Exception e) { throw new IOException("Error stopping client", e); }
    }
}

