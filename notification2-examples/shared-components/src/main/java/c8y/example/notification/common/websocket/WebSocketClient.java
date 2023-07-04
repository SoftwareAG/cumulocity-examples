package c8y.example.notification.common.websocket;

public interface WebSocketClient extends AutoCloseable {

    void connect() throws Exception;
}
