package c8y.remoteaccess;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;

import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;

public class RemoteAccessDriver implements Driver {

    private Platform platform;

    @Override
    public void initialize() throws Exception {
        // you can do driver specific initialization
    }

    @Override
    public void initialize(Platform platform) throws Exception {
        // here you get the platform for access to the API
        this.platform = platform;
    }

    @Override
    public OperationExecutor[] getSupportedOperations() {
        // here you return an array of all the OperationExecutors this driver supports
        return new OperationExecutor[] { new RemoteAccessOperationExecutor(platform) };
    }

    @Override
    public void initializeInventory(ManagedObjectRepresentation mo) {
        // this is used if you would like to persist some data to the managedObject of the device (before MO update)
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
