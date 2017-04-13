package c8y.remoteaccess.tunnel;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketClient extends Endpoint {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketClient.class);

    private Session session;

    private VncSocketClient vncClient;

    public WebSocketClient() {
        super();
    }

    public void sendMessage(ByteBuffer data) throws IOException {
        if (session != null && session.getBasicRemote() != null) {
            session.getBasicRemote().sendBinary(data);
        } else {
            logger.debug("Unable to send message. No basic remote!");
        }
    }

    public void close() throws IOException {
        logger.debug("Closing websocket.");
        if (session != null) {
            session.close();
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        logger.debug("Websocket was opened.");
        this.session = session;
        session.addMessageHandler(new MessageHandler.Whole<ByteBuffer>() {

            @Override
            public void onMessage(ByteBuffer message) {
                onBinaryMessage(message);
            }
        });
    }

    public void onBinaryMessage(ByteBuffer data) {
        logger.debug("Received " + data.remaining() + " bytes from websocket, Forwarding to VNC...");
        try {
            vncClient.sendMessage(data.array());
        } catch (IOException e) {
            logger.error("Unable to forward message to VNC");
            try {
                vncClient.close();
                close();
            } catch (IOException e1) {
                logger.debug("Exception when closing vnc client connection: ", e.getMessage());
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable e) {
        logger.error("Websocket[" + session.getId() + "] encountered an error:", e);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.debug("Websocket[" + session.getId() + "] was closed.");
        this.session = null;
    }

    public void setVncClient(VncSocketClient vncClient) {
        this.vncClient = vncClient;
    }

    public boolean isClosed() {
        return session == null;
    }
}
