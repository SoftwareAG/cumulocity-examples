package c8y.remoteaccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;

import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;

public class RemoteAccessDriver implements Driver {

    private static final Logger logger = LoggerFactory.getLogger(RemoteAccessDriver.class);

    private RemoteAccessOperationExecutor connectOperationExecutor;

    @Override
    public void initialize() throws Exception {
    }

    @Override
    public void initialize(Platform platform) throws Exception {
        logger.debug("Initializing with platform " + platform);
        connectOperationExecutor = new RemoteAccessOperationExecutor(platform);
    }

    @Override
    public OperationExecutor[] getSupportedOperations() {
        logger.debug("Returning supported operations");
        return new OperationExecutor[] { connectOperationExecutor };
    }

    @Override
    public void initializeInventory(ManagedObjectRepresentation mo) {
        logger.debug("Initializing inventory");
        OpsUtil.addSupportedOperation(mo, connectOperationExecutor.supportedOperationType());
    }

    @Override
    public void discoverChildren(ManagedObjectRepresentation mo) {
    }

    @Override
    public void start() {
    }
}
