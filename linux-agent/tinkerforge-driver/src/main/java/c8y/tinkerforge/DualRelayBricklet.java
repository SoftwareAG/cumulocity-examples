package c8y.tinkerforge;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.tinkerforge.BrickletDualRelay;

import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;

public class DualRelayBricklet implements Driver {

	public DualRelayBricklet(String uid, BrickletDualRelay brickletDualRelay) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(Platform platform) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation mo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

}
