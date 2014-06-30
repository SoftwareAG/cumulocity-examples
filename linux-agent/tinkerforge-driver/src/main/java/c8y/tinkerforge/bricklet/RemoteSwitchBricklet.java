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
import com.tinkerforge.BrickletRemoteSwitch;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import c8y.lx.driver.DeviceManagedObject;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;
import c8y.tinkerforge.TFIds;

public class RemoteSwitchBricklet implements Driver {
	
	private static final String TYPE = "RemoteSwitch";
	private static final String TYPE_A_SWITCH_OP_TYPE = "c8y_typeA_switch";
	private static final String TYPE_B_SWITCH_OP_TYPE = "c8y_typeB_switch";
	private static final String TYPE_B_DIM_OP_TYPE = "c8y_typeB_dim";
	private static final String TYPE_C_SWITCH_OP_TYPE = "c8y_typeC_switch";
	
	private static final Logger logger = LoggerFactory
			.getLogger(RemoteSwitchBricklet.class);
	
	private String id;
	private BrickletRemoteSwitch remoteSwitch;
	private ManagedObjectRepresentation remoteSwitchMo = 
			new ManagedObjectRepresentation();
	private Platform platform;
	
	public RemoteSwitchBricklet(String id, BrickletRemoteSwitch remoteSwitch) {
		this.id=id;
		this.remoteSwitch = remoteSwitch;
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
		return new OperationExecutor[] { new TypeASocketSwitchOperationExecutor(),
				new TypeBSocketSwitchOperationExecutor(),
				new TypeBSocketDimOperationExecutor(),
				new TypeCSocketSwitchOperationExecutor()};
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		// Nothing to be done here.

	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation parent) {
		try {
			remoteSwitchMo.set(TFIds.getHardware(remoteSwitch, TYPE));
		} catch (TimeoutException | NotConnectedException e) {
			logger.warn("Cannot read hardware parameters", e);
		}
		
		remoteSwitchMo.setType(TFIds.getType(TYPE));
		remoteSwitchMo.setName(TFIds.getDefaultName(parent.getName(), TYPE, id));
		
		for(OperationExecutor operation:getSupportedOperations())
			OpsUtil.addSupportedOperation(remoteSwitchMo, operation.supportedOperationType());
		
		try {
			DeviceManagedObject dmo = new DeviceManagedObject(platform);
			dmo.createOrUpdate(remoteSwitchMo, TFIds.getXtId(id), parent.getId());
		} catch (SDKException e) {
			logger.warn("Cannot create remote switch object", e);
		}
		
	}

	@Override
	public void start() {
		// Nothing to be done here.

	}

	class TypeASocketSwitchOperationExecutor implements OperationExecutor{

		@Override
		public String supportedOperationType() {
			return TYPE_A_SWITCH_OP_TYPE;
		}

		@Override
		public void execute(OperationRepresentation operation, boolean cleanup)
				throws Exception {
			if (cleanup) 
				operation.setStatus(OperationStatus.FAILED.toString());
			
			remoteSwitch.switchSocketA( (short)operation.getProperty("houseCode"),
					(short)operation.getProperty("recieverCode"), 
					(short)operation.getProperty("switchTo"));
			
			operation.setStatus(OperationStatus.SUCCESSFUL.toString());
		}
		
	}

	class TypeBSocketSwitchOperationExecutor implements OperationExecutor{

		@Override
		public String supportedOperationType() {
			return TYPE_B_SWITCH_OP_TYPE;
		}

		@Override
		public void execute(OperationRepresentation operation, boolean cleanup)
				throws Exception {
			if (cleanup)
				operation.setStatus(OperationStatus.FAILED.toString());
			
			
			remoteSwitch.switchSocketB( (long)operation.getProperty("address"),
					(short)operation.getProperty("unit"),
					(short)operation.getProperty("switchTo"));
			
			operation.setStatus(OperationStatus.SUCCESSFUL.toString());
		}
		
	}

	class TypeBSocketDimOperationExecutor implements OperationExecutor{

		@Override
		public String supportedOperationType() {
			return TYPE_B_DIM_OP_TYPE;
		}

		@Override
		public void execute(OperationRepresentation operation, boolean cleanup)
				throws Exception {
			if (cleanup)
				operation.setStatus(OperationStatus.FAILED.toString());
			
			remoteSwitch.dimSocketB((long)operation.getProperty("address"), 
									(short)operation.getProperty("unit"), 
									(short)operation.getProperty("dimValue"));
			
			operation.setStatus(OperationStatus.SUCCESSFUL.toString());
		}
		
	}

	class TypeCSocketSwitchOperationExecutor implements OperationExecutor{

		@Override
		public String supportedOperationType() {
			return TYPE_C_SWITCH_OP_TYPE;
		}
		
		

		@Override
		public void execute(OperationRepresentation operation, boolean cleanup)
				throws Exception {
			if (cleanup)
				operation.setStatus(OperationStatus.FAILED.toString());
			
			remoteSwitch.switchSocketC((char)operation.getProperty("systemCode"), 
					(short)operation.getProperty("deviceCode"), 
					(short)operation.getProperty("switchTo"));
			
			operation.setStatus(OperationStatus.SUCCESSFUL.toString());
		}
		
	}
}
