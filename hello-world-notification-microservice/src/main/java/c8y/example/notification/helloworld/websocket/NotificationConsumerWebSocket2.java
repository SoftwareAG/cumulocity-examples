package c8y.example.notification.helloworld.websocket;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class NotificationConsumerWebSocket2 implements Closeable {

    private final NotificationCallback callback;
    private NotificationWebSocketClient client;

    public NotificationConsumerWebSocket2(NotificationCallback callback) {
        this.callback = callback;
    }

    public void run(URI destUri, int maxIdle, int maxRetries) throws IOException, InterruptedException {

        client = new NotificationWebSocketClient(destUri, this.callback);
        for (int retries = 0; maxRetries == 0 || retries < maxRetries; retries++)
        {
            if(!client.isOpen() && !client.isClosing()) {
                client.connectBlocking();
                return;
            }
        }
        // TODO maybe close if idle
    }

    // TODO is there a Spring shutdown-hook to call this
    // TODO should we expose close(int,String)  - instead of Closeable or in addition!
    @Override
    public void close(){
        client.close(CloseFrame.NORMAL, "client closed");
    }


    private static class NotificationWebSocketClient extends WebSocketClient {

        private final NotificationCallback callback;

        NotificationWebSocketClient(URI serverUri, NotificationCallback callback)
        {
            super(serverUri);
            this.callback = callback;
        }

        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            // TODO are the serverHandshake details of any interest at all?
            log.info("Websocket connected: " + serverHandshake.getHttpStatusMessage() + ", code: " + serverHandshake.getHttpStatus());
        }

        @Override
        public void onMessage(String message) {
            ParseResult parsed = ParseResult.parse(message);
            callback.onNotification(parsed.notificationHeaders, parsed.message);
            if(parsed.ackHeader != null){
                try{
                    send(parsed.ackHeader); // ack message
                } catch (Exception e){
                    log.error("Failed to ack message "+parsed.ackHeader, e);
                }
            }
            else{
                log.warn("No message id found for ack");
            }
        }

        @Override
        public void onClose(int statusCode, String reason, boolean remote) {
            log.info("WebSocket closed " + (remote ? "by server. " : "") + " Code:" + statusCode + ", reason: " + reason);
            callback.close();
        }

        @Override
        public void onError(Exception e) {
            log.error("WebSocket error:" + e, e);
        }
    }

    private static class ParseResult {
        private final String ackHeader;
        private final List<String> notificationHeaders;
        private final String message;

        private ParseResult(String ackHeader, List<String>notificationHeaders, String message){
            this.ackHeader = ackHeader;
            this.notificationHeaders = notificationHeaders;
            this.message = message;
        }

        static ParseResult parse(String message){
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
            if (headers.isEmpty()) {
                return new ParseResult(null, headers, message);
            }
            return new ParseResult(headers.get(0), headers.subList(1, headers.size()), message);
        }
    }
}

