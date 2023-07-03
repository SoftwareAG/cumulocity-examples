package c8y.example.common.websocket;

public interface WebSocketClient extends AutoCloseable {

    void connect() throws Exception;
}
