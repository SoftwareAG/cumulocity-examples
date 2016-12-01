package c8y.remoteaccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;

import c8y.RemoteAccessConnect;
import c8y.lx.driver.OperationExecutor;

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
    public void execute(OperationRepresentation operation, boolean cleanup) throws Exception {
        logger.info("Received operation {}", operation);
        RemoteAccessConnect connect = operation.get(RemoteAccessConnect.class);
        if (connect == null) {
            throw new IllegalArgumentException("Fragement c8y_RemoteAccessConnect not present");
        }

        if (!cleanup) {
            String hostname = connect.getHostname();
            Integer port = connect.getPort();
            String protocol = connect.getProtocol();
            String connectionKey = connect.getConnectionKey();
            logger.info("Starting connect operation to {}:{} with protocol {}", hostname, port, protocol);
        }
    }
}
