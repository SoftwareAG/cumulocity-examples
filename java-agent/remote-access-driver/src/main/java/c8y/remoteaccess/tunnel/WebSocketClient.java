package c8y.remoteaccess.tunnel;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.websocket.ClientEndpoint;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ClientEndpoint
public class WebSocketClient extends Endpoint {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketClient.class);

    private Session session;

    private VncSocketClient vpnClient;

    public void sendMessage(byte[] data) throws IOException {
        session.getBasicRemote().sendBinary(ByteBuffer.wrap(data));
    }

    public void sendMessage(ByteBuffer data) throws IOException {
        if (session != null && session.getBasicRemote() != null) {
            session.getBasicRemote().sendBinary(data);
        }
    }

    public void close() throws IOException {
        if (session != null) {
            session.close();
        }
    }

    @OnOpen
    public void open(Session session) throws IOException {
        logger.info("Websocket[" + session.getId() + "] was opened.");
        this.session = session;
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        logger.debug("Websocket[" + session.getId() + "] recieved:" + message);
    }

    @OnMessage
    public void onBinary(ByteBuffer data, Session session) {
        logger.debug("Received " + data.remaining() + " bytes from websocket");
        try {
            vpnClient.sendMessage(data.array());
        } catch (IOException e) {
            logger.error("Unable to forward message to vpn");
            try {
                vpnClient.close();
                close();
            } catch (IOException e1) {
                // Ignore
            }
        }
    }

    @OnError
    public void error(Session session, Throwable e) {
        logger.error("Websocket[" + session.getId() + "] encountered an error:");
        e.printStackTrace();
    }

    @OnClose
    public void close(Session session) {
        logger.info("Websocket[" + session.getId() + "] was closed.");
        this.session = null;
    }

    public void setVpnClient(VncSocketClient vpnClient) {
        this.vpnClient = vpnClient;
    }

    public boolean isClosed() {
        return session == null;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        // TODO Auto-generated method stub
    }
}
