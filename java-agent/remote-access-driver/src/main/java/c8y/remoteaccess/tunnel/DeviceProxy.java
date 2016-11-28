package c8y.remoteaccess.tunnel;

import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceProxy {

    private static final Logger logger = LoggerFactory.getLogger(DeviceProxy.class);

    public static final String DEFAULT_VNC_HOST = "localhost";

    public static final int DEFAULT_VNC_PORT = 5901;

    public static final String DEFAULT_WEBSOCKET_URL = "ws://localhost:8080/vnc/device";
    
    public static void main(String[] args) throws Exception {

        //!TODO: Add realtime feature
        // http://cumulocity.com/guides/java/developing/
        logger.info("DeviceProxy starting");

        String vncHost = DEFAULT_VNC_HOST;
        int vncPort = DEFAULT_VNC_PORT;
        String websocketUrl = DEFAULT_WEBSOCKET_URL;

        if (args.length == 0) {
            System.out.println("Usage: DeviceProxy <ConnectionKey> [VNC hostanme] [VNC port] [Websocket URL]");
            return;
        }

        String connectionKey = args[0];

        if (args.length > 1) {
            vncHost = args[1];

            if (args.length > 2) {
                vncPort = Integer.parseInt(args[2]);

                if (args.length > 3) {
                    websocketUrl = args[3];
                }
            }
        }

        // Connect to websocket
        logger.info("Creating websocket connection " + websocketUrl + "/" + connectionKey);
        WebSocketClient websocket = new WebSocketClient();
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(websocket, new URI(websocketUrl + "/" + connectionKey));

        // Connect to VNC
        logger.info("Creating VNC connection " + vncHost + ":" + vncPort);
        VncSocketClient vncsocket = new VncSocketClient(vncHost, vncPort);
        vncsocket.connect();

        // Start tunneling thread
        logger.info("Starting tunneling thread");
        TunnelingThread thread = new TunnelingThread(websocket, vncsocket);
        thread.start();

        logger.info("Tunneling operational");
        while (!websocket.isClosed()) {
            Thread.sleep(100);
        }

        logger.info("Stopping tunneling thread");
        thread.stop();

        logger.info("Closing VNC connection");
        vncsocket.close();

        logger.info("DeviceProxy finished");
    }
}
