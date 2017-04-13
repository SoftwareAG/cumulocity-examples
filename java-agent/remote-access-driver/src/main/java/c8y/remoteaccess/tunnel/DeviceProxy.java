package c8y.remoteaccess.tunnel;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformParameters;

import c8y.remoteaccess.tunnel.TunnelingThread;
import c8y.remoteaccess.tunnel.VncSocketClient;
import c8y.remoteaccess.RemoteAccessException;
import c8y.remoteaccess.RemoteAccessVncException;
import c8y.remoteaccess.RemoteAccessWebsocketException;
import c8y.remoteaccess.tunnel.AuthHeaderConfigurator;

public class DeviceProxy {

    private static final String WEBSOCKET_DEVICE_ENDPOINT = "/service/remoteaccess/device/";

    private static final Logger logger = LoggerFactory.getLogger(DeviceProxy.class);

    private String vncHost;

    private int vncPort;

    private String websocketHost;

    private int websocketPort;

    private String connectionKey;

    private boolean encrypted;

    private TunnelingThread thread;

    private VncSocketClient vncsocket;

    private String username;

    private String password;

    private WebSocketClient websocket;

    public DeviceProxy(String hostname, int port, String connectionKey, Platform platform) throws MalformedURLException {

        if (!(platform instanceof PlatformParameters)) {
            throw new IllegalArgumentException("Unable to determine host by platform: " + platform);
        }

        PlatformParameters parameters = (PlatformParameters) platform;
        URL url = new URL(parameters.getHost());

        this.encrypted = url.getProtocol().equals("https");
        this.websocketHost = url.getHost();
        this.websocketPort = url.getPort();

        this.username = parameters.getPrincipal();
        this.password = parameters.getPassword();

        this.vncHost = hostname;
        this.vncPort = port;
        this.connectionKey = connectionKey;
    }

    private URI getWebsocketEndpoint() {
        String protocol = (encrypted ? "wss://" : "ws://");
        String hostname = websocketHost + ((websocketPort > 0) ? (":" + websocketPort) : "");
        return URI.create(protocol + hostname + WEBSOCKET_DEVICE_ENDPOINT + connectionKey);
    }

    public void start() throws RemoteAccessException {

        logger.debug("DeviceProxy starting");

        try {
            URI endpoint = getWebsocketEndpoint();

            try {
                // Connect to websocket
                logger.debug("Creating websocket connection " + endpoint);
                websocket = new WebSocketClient();

                ClientEndpointConfig endpointConfig = ClientEndpointConfig.Builder.create()
                        .configurator(new AuthHeaderConfigurator(username, password)).preferredSubprotocols(Arrays.asList("binary"))
                        .build();
                ContainerProvider.getWebSocketContainer().connectToServer(websocket, endpointConfig, endpoint);
            } catch (IOException | DeploymentException e) {
                logger.error("Websocket connect error:", e.getMessage());
                throw new RemoteAccessWebsocketException(e.getMessage());
            }

            try {
                // Connect to VNC
                logger.debug("Creating VNC connection " + vncHost + ":" + vncPort);
                vncsocket = new VncSocketClient(vncHost, vncPort);
                vncsocket.connect();
            } catch (IOException e) {
                logger.error("Vnc connect error:", e.getMessage());
                throw new RemoteAccessVncException(e.getMessage());
            }

            // Start tunneling thread
            logger.debug("Starting tunneling thread");
            thread = new TunnelingThread(websocket, vncsocket);
            thread.start();

            logger.debug("Tunneling operational");

        } catch (RemoteAccessException e) {
            stop();
            throw e;
        }
    }

    public void stop() throws RemoteAccessException {
        logger.info("Stopping tunnel...");

        try {
            if (thread != null) {
                logger.debug("Stopping tunneling thread");
                thread.stop();
                thread = null;
            }

            if (vncsocket != null) {
                logger.debug("Closing VNC connection");
                vncsocket.close();
                vncsocket = null;
            }

            if (websocket != null) {
                logger.debug("Closing websocket connection");
                websocket.close();
                websocket = null;
            }
        } catch (IOException e) {
            throw new RemoteAccessException(e.getMessage());
        }
    }
}
