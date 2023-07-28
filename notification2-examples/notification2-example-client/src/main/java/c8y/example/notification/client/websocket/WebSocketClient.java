package c8y.example.notification.client.websocket;

public interface WebSocketClient extends AutoCloseable {

    void connect() throws Exception;
}
