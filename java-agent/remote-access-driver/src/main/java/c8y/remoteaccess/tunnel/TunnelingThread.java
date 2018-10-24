package c8y.remoteaccess.tunnel;

import c8y.remoteaccess.util.CumulocityIOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static c8y.remoteaccess.util.CumulocityIOUtils.EOF;

public class TunnelingThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TunnelingThread.class);

    private WebSocketClient websocket;

    private final DeviceSocketClient deviceSocket;

    private boolean close;

    public TunnelingThread(WebSocketClient websocket, DeviceSocketClient deviceSocketClient) {
        this.websocket = websocket;
        this.deviceSocket = deviceSocketClient;
        //websocket.setDeviceClient(deviceSocketClient);
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
        close = false;
    }

    public void stop() {
        logger.debug("Stopping tunneling thread");
        close = true;
        CumulocityIOUtils.closeQuietly(websocket, deviceSocket);
    }

    @Override
    public void run() {
        close = false;
        byte[] data = new byte[10 * 1024];
        while (!close) {
            try {
                int bytesRead = deviceSocket.read(data);
                if (bytesRead > 0) {
                    logger.debug("Received {} bytes from device server. Forwarding to websocket...", bytesRead);
                    if (websocket == null) {
                        throw new IllegalStateException("Not connected to Websocket");
                    }
                    websocket.sendMessage(ByteBuffer.wrap(data, 0, bytesRead));
                } else if (bytesRead == EOF) {
                    logger.debug("Encountered end of stream. Closing connection...");
                    stop();
                }
            } catch (Exception e) {
                logger.debug("Device data forwarding error: {}", e.getMessage(), e);
                stop();
            }
        }
    }
}
