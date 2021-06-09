package c8y.example.notification.helloworld;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

@Deprecated
@Slf4j
public class NotificationWebSocketClient extends WebSocketClient {

    public NotificationWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        //send("Hello, it is me. Mario :)");
        log.info("opened connection");
    }

    @Override
    public void onMessage(String message) {
        log.info("Message: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info(
                "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: "
                        + reason);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }
}
