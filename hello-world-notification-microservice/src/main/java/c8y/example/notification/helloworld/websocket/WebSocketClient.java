package c8y.example.notification.helloworld.websocket;

public interface WebSocketClient extends AutoCloseable {

    void connect() throws Exception;
}
