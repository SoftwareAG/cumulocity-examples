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
import com.tinkerforge.BrickletIO16;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import c8y.lx.driver.DeviceManagedObject;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;
import c8y.tinkerforge.TFIds;

public class IO16Bricklet implements Driver {

	private static final String TYPE = "IO16";
	private static final String SET_PORT_OP_TYPE = "c8y_SetPort";
	
	private static final Logger logger = LoggerFactory
			.getLogger(IO16Bricklet.class);
	
	private String id;
	private BrickletIO16 io16;
	private ManagedObjectRepresentation io16Mo = 
			new ManagedObjectRepresentation();
	private Platform platform;
	
	public IO16Bricklet(String uid, BrickletIO16 io16) {
		this.id = uid;
		this.io16 = io16;
	}
	
	@Override
	public void initialize() throws Exception {
		// Nothing to be done here.
	}

	@Override
	public void initialize(Platform platform) throws Exception {
		this.platform = platform;
	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[] { new SetPortOperationExecutor() };
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		// Nothing to be done here.
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation parent) {
		try {
			io16Mo.set(TFIds.getHardware(io16, TYPE));
		} catch (TimeoutException | NotConnectedException e) {
			logger.warn("Cannot read hardware parameters", e);
		}
		
		io16Mo.setType(TFIds.getType(TYPE));
		io16Mo.setName(TFIds.getDefaultName(parent.getName(), TYPE, id));
		
		for(OperationExecutor operation:getSupportedOperations())
			OpsUtil.addSupportedOperation(io16Mo, operation.supportedOperationType());
		
		try {
			DeviceManagedObject dmo = new DeviceManagedObject(platform);
			dmo.createOrUpdate(io16Mo, TFIds.getXtId(id), parent.getId());
		} catch (SDKException e) {
			logger.warn("Cannot create remote switch object", e);
		}
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}
	
	class SetPortOperationExecutor implements OperationExecutor{

		@Override
		public String supportedOperationType() {
			return SET_PORT_OP_TYPE;
		}

		@Override
		public void execute(OperationRepresentation operation, boolean cleanup)
				throws Exception {
			if (cleanup)
				operation.setStatus(OperationStatus.FAILED.toString());
			
			io16.setPortConfiguration((char)operation.getProperty("port"), 
					(short)operation.getProperty("selectionMask"), 
					(char)operation.getProperty("direction"), 
					(boolean)operation.getProperty("value") );
			
			operation.setStatus(OperationStatus.SUCCESSFUL.toString());
		}
		
	}

}
