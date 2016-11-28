package c8y.remoteaccess.tunnel;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TunnelingThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TunnelingThread.class);

    private WebSocketClient websocket;

    private final VncSocketClient vpnsocket;

    private boolean close;

    public TunnelingThread(WebSocketClient websocket, VncSocketClient vpnsocket) {
        this.websocket = websocket;
        this.vpnsocket = vpnsocket;
        websocket.setVpnClient(vpnsocket);
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        close = true;
    }

    @Override
    public void run() {
        close = false;
        byte[] data = new byte[10 * 1024];
        while (!close) {
            try {
                int bytesRead = vpnsocket.read(data);
                if (bytesRead > 0) {
                    logger.debug("Received " + bytesRead + " bytes from VNC server");
                    if (websocket == null) {
                        throw new IllegalStateException("Not connect to Websocket");
                    }
                    logger.debug("Forwarding " + bytesRead + " to websocket");
                    websocket.sendMessage(ByteBuffer.wrap(data, 0, bytesRead));
                } else if (bytesRead == -1) {
                    close = true;
                }
            } catch (IOException e) {
                close = true;
            }
        }
    }
}
