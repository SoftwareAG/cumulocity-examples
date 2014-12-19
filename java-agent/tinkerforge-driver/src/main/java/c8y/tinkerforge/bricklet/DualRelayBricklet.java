/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package c8y.tinkerforge.bricklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import c8y.Hardware;
import c8y.RelayArray;
import c8y.lx.driver.DeviceManagedObject;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;
import c8y.tinkerforge.TFIds;

public class DualRelayBricklet implements Driver {
	
	private static final String TYPE = "DualRelay";
	
	private static final Logger logger = LoggerFactory
			.getLogger(DualRelayBricklet.class);
	
	private String id;
	private BrickletDualRelay dualRelay;
	private ManagedObjectRepresentation dualRelayMo = 
			new ManagedObjectRepresentation();
	private Platform platform;
	private RelayArray relayState = new RelayArray();

	public DualRelayBricklet(String uid, BrickletDualRelay brickletDualRelay) {
		this.id=uid;
		this.dualRelay=brickletDualRelay;
	}
	
    @Override
    public void initialize() throws Exception {
        // Nothing to be done here.
    }

	@Override
	public void initialize(Platform platform) throws Exception {
		this.platform=platform;
	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[] {new SetStateOperationExecutor()};
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		// Nothing to be done here.
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation parent) {
		try {
			dualRelayMo.set(TFIds.getHardware(dualRelay, TYPE));
		} catch (TimeoutException | NotConnectedException e) {
			logger.warn("Cannot read hardware parameters", e);
		}
		
		dualRelayMo.setType(TFIds.getType(TYPE));
		dualRelayMo.setName(TFIds.getDefaultName(parent.getName(), TYPE, id));
		
		for(OperationExecutor operation:getSupportedOperations())
			OpsUtil.addSupportedOperation(dualRelayMo, operation.supportedOperationType());
		
		try {
			DeviceManagedObject dmo = new DeviceManagedObject(platform);
			dmo.createOrUpdate(dualRelayMo, TFIds.getXtId(id, parent.get(Hardware.class).getSerialNumber()), parent.getId());
		} catch (SDKException e) {
			logger.warn("Cannot create or update MO", e);
		}
	}

	@Override
	public void start() {
		/*
		 * The state is updated.
		 * If there is no state in the inventory, it's initialized first.
		 */
		relayState=dualRelayMo.get(RelayArray.class);
		if(relayState==null){
			relayState=new RelayArray();
			relayState.add("OPEN");
			relayState.add("OPEN");
			try {
				//Needed to avoid updating all fields and reduce overhead
				ManagedObjectRepresentation updateMo = new ManagedObjectRepresentation();
				updateMo.setId(dualRelayMo.getId());
				updateMo.set(relayState);
				platform.getInventoryApi().update(updateMo);
			} catch(SDKException e) {
				logger.warn("Couldn't update device state on the platform", e);
			}
		}	
		updateState();
	}
	
	class SetStateOperationExecutor implements OperationExecutor{

		@Override
		public String supportedOperationType() {
			return "c8y_RelayArray";
		}

		@Override
		public void execute(OperationRepresentation operation, boolean cleanup)
				throws Exception {
			if (!dualRelayMo.getId().equals(operation.getDeviceId())) {
				// Silently ignore the operation if it is not targeted to us,
				// another driver will (hopefully) care.
				return;
			}
			if (cleanup)
				operation.setStatus(OperationStatus.FAILED.toString());
			
			relayState=operation.get(RelayArray.class);
			while(relayState.size()>2)
				relayState.remove(relayState.size()-1);
			updateState();
			
			/*
			 * State is persisted after each change.
			 */
			dualRelayMo.set(relayState);
			//Needed in order to reduce overhead
			ManagedObjectRepresentation updateMo = new ManagedObjectRepresentation();
			updateMo.setId(dualRelayMo.getId());
			updateMo.set(relayState);
			platform.getInventoryApi().update(updateMo);
			
			operation.setStatus(OperationStatus.SUCCESSFUL.toString());
		}
		
	}
	
	private void updateState(){
		try {
			dualRelay.setState("CLOSED".equalsIgnoreCase(relayState.get(0)), 
					"CLOSED".equalsIgnoreCase(relayState.get(1)));
		} catch (TimeoutException | NotConnectedException e) {
			logger.warn("Couldn't update state", e);
		}
	}

}