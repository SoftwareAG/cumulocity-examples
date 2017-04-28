package c8y.remoteaccess.tunnel;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TunnelingThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TunnelingThread.class);

    private WebSocketClient websocket;

    private final VncSocketClient vncsocket;

    private boolean close;

    public TunnelingThread(WebSocketClient websocket, VncSocketClient vncsocket) {
        this.websocket = websocket;
        this.vncsocket = vncsocket;
        websocket.setVncClient(vncsocket);
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
        close = false;
    }

    public boolean isActive() {
        return !close;
    }

    public void stop() {
        logger.debug("Stopping tunneling thread");
        close = true;
    }

    @Override
    public void run() {
        close = false;
        byte[] data = new byte[10 * 1024];
        while (!close) {
            try {
                int bytesRead = vncsocket.read(data);
                if (bytesRead > 0) {
                    logger.debug("Received " + bytesRead + " bytes from VNC server. Forwarding to websocket...");
                    if (websocket == null) {
                        throw new IllegalStateException("Not connect to Websocket");
                    }
                    websocket.sendMessage(ByteBuffer.wrap(data, 0, bytesRead));
                } else if (bytesRead == -1) {
                    logger.debug("Encountered end of stream. Closing connection...");
                    stop();
                }
            } catch (SocketException e) {
                logger.debug(e.getMessage());
            } catch (IOException e) {
                logger.debug("Error:", e);
                stop();
            }
        }
    }
}
