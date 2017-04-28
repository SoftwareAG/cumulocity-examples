package c8y.remoteaccess;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.websocket.DeploymentException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;

import c8y.RemoteAccessConnect;
import c8y.lx.driver.OperationExecutor;
import c8y.remoteaccess.tunnel.DeviceProxy;

public class RemoteAccessOperationExecutor implements OperationExecutor {

    private static final Logger logger = LoggerFactory.getLogger(RemoteAccessOperationExecutor.class);

    private static final String OPERATION_REMOTEACCESS_CONNECT = "c8y_RemoteAccessConnect";

    private Platform platform;

    public RemoteAccessOperationExecutor(Platform platform) {
        this.platform = platform;
    }

    @Override
    public String supportedOperationType() {
        return OPERATION_REMOTEACCESS_CONNECT;
    }

    @Override
    public void execute(OperationRepresentation operation, boolean cleanup) throws MalformedURLException {
        logger.debug("Received operation {}", operation);
        RemoteAccessConnect connect = operation.get(RemoteAccessConnect.class);
        if (connect == null) {
            throw new IllegalArgumentException("Fragement c8y_RemoteAccessConnect not present");
        }

        if (!cleanup) {
            try {
                String hostname = connect.getHostname();
                Integer port = connect.getPort();
                String connectionKey = connect.getConnectionKey();

                logger.debug("Starting connect operation to {}:{} with connection key {}", hostname, port, connectionKey);
                DeviceProxy proxy = new DeviceProxy(hostname, port, connectionKey, platform);
                proxy.start();

                operation.setStatus(OperationStatus.SUCCESSFUL.toString());

            } catch (RemoteAccessWebsocketException e) {
                operation.setStatus(OperationStatus.FAILED.toString());
                operation.setFailureReason("Device Agent websocket error: " + e.getMessage());
            } catch (RemoteAccessVncException e) {
                operation.setStatus(OperationStatus.FAILED.toString());
                operation.setFailureReason("Device Agent VNC error: " + e.getMessage());
            } catch (RemoteAccessException e) {
                operation.setStatus(OperationStatus.FAILED.toString());
                operation.setFailureReason("Device Agent generic error: " + e.getMessage());
            }
        }
    }
}
