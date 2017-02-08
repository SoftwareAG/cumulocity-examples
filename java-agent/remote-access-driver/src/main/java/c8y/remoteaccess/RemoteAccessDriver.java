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
        // you can do driver specific initialization
    }

    @Override
    public void initialize(Platform platform) throws Exception {
        // here you get the platform for access to the API
        logger.debug("Initializing with platform " + platform);
        connectOperationExecutor = new RemoteAccessOperationExecutor(platform);
    }

    @Override
    public OperationExecutor[] getSupportedOperations() {
        // here you return an array of all the OperationExecutors this driver supports
        logger.debug("Returning supported operations");
        return new OperationExecutor[] { connectOperationExecutor };
    }

    @Override
    public void initializeInventory(ManagedObjectRepresentation mo) {
        // this is used if you would like to persist some data to the managedObject of the device (before MO update)
        logger.debug("Initializing inventory");
        OpsUtil.addSupportedOperation(mo, connectOperationExecutor.supportedOperationType());
    }

    @Override
    public void discoverChildren(ManagedObjectRepresentation mo) {
        // this is the MO of the device, you can get MOid from it, etc (after MO update)
    }

    @Override
    public void start() {
        // and that's where the driver magic happens if any xD
    }
}
