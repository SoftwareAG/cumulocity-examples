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

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.tinkerforge.BrickletIO16;
import com.tinkerforge.BrickletIO16.PortConfiguration;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import c8y.Relay.RelayState;
import c8y.RelayArray;
import c8y.lx.driver.Configurable;
import c8y.lx.driver.DeviceManagedObject;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;
import c8y.tinkerforge.TFIds;

public class IO16Bricklet implements Driver, Configurable {

	class Port{
		protected final static String DIRECTION_PROP = ".direction";
		protected final static String VALUE_PROP= ".value";
		protected char name;
		protected PortConfiguration defaultConfiguration;
		protected PortConfiguration actualConfiguration;
		
		public Port(char name, short defaultDirection, short defaultValue) {
			this.name=name;
			this.defaultConfiguration.directionMask = defaultDirection;
			this.defaultConfiguration.valueMask = defaultValue;
		}
	}
	
	private static final String TYPE = "IO16";
	
	private static final Logger logger = LoggerFactory
			.getLogger(IO16Bricklet.class);
	
	private String id;
	private BrickletIO16 io16;
	private ManagedObjectRepresentation io16Mo = 
			new ManagedObjectRepresentation();
	private Platform platform;
	
	/*
	 * direction: 0-Output 1-input
	 * value: OUTPUT: 0,1-logical values INPUT: 0-default 1-pull up
	 * 
	 * With this in mind the configuration below does the following:
	 * Configures all pins on portA as input with pull ups and 
	 * all pins on portB as output set to 0 logical level.
	 */
	private Port[] ports = new Port[]{	new Port('a', (short)0b11111111, (short)0b11111111), 
										new Port('b', (short)0b00000000, (short)0b00000000)  };
	
	public IO16Bricklet(String uid, BrickletIO16 io16) {
		this.id = uid;
		this.io16 = io16;
	}
	
	@Override
	public void addDefaults(Properties props) {
		for(Port port:ports){
			props.setProperty(TFIds.getPropertyName(TYPE)+".port"+port.name+Port.DIRECTION_PROP, 
					Short.toString(port.defaultConfiguration.directionMask));
			props.setProperty(TFIds.getPropertyName(TYPE)+".port"+port.name+Port.VALUE_PROP, 
					Short.toString(port.defaultConfiguration.valueMask));
		}
	}
	
	@Override
	public void configurationChanged(Properties props) {
		for(Port port:ports){
			port.actualConfiguration.directionMask=Short.parseShort( props.getProperty(
					TFIds.getPropertyName(TYPE)+".port"+port.name+Port.DIRECTION_PROP, 
					Short.toString(port.defaultConfiguration.directionMask )));
			port.actualConfiguration.valueMask=Short.parseShort( props.getProperty(
					TFIds.getPropertyName(TYPE)+".port"+port.name+Port.VALUE_PROP, 
					Short.toString(port.defaultConfiguration.valueMask )));
			setPort(port);
		}
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
		return new OperationExecutor[] { new RelayArrayOperationExecutor() };
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
		// Nothing to be done.
		
	}
	
	class RelayArrayOperationExecutor implements OperationExecutor{

		@Override
		public String supportedOperationType() {
			return "c8y_RelayArray";
		}

		@Override
		public void execute(OperationRepresentation operation, boolean cleanup)
				throws Exception {
			if (cleanup)
				operation.setStatus(OperationStatus.FAILED.toString());
			
			RelayArray relayArray = operation.get(RelayArray.class);
			updatePorts(relayArray);
			for(Port p:ports)
				setPort(p);
			
			operation.setStatus(OperationStatus.SUCCESSFUL.toString());
		}
		
	}
	
	private void setPort(Port port){
		try{
			io16.setPortConfiguration(port.name, (short)(port.actualConfiguration.directionMask&port.actualConfiguration.valueMask), BrickletIO16.DIRECTION_IN, true);
			io16.setPortConfiguration(port.name, (short)(port.actualConfiguration.directionMask&negate(port.actualConfiguration.valueMask)), BrickletIO16.DIRECTION_IN, false);
			io16.setPortConfiguration(port.name, (short)(negate(port.actualConfiguration.directionMask)&port.actualConfiguration.valueMask), BrickletIO16.DIRECTION_OUT, true);
			io16.setPortConfiguration(port.name, (short)(negate(port.actualConfiguration.directionMask)&negate(port.actualConfiguration.valueMask)), BrickletIO16.DIRECTION_OUT, false);
		} catch (Exception x){
			logger.warn("Error setting port", x);
		}
	}
	
	private short negate(short s){
		return (short)((short)0b11111111-s);
	}
	/*
	 * The idea is to update the values of output pins only(starting from pin A0 up to pin B7) using the 
	 * relay values(CLOSED - logical true).
	 */
	private void updatePorts(RelayArray relayArray){
		boolean direction[] = createBooleanArray(ports[0].actualConfiguration.directionMask, ports[1].actualConfiguration.directionMask);
		boolean value[] = createBooleanArray(ports[0].actualConfiguration.valueMask, ports[1].actualConfiguration.valueMask);
		
		for(int i=0; i<direction.length; i++)
			if(!direction[i]&&!relayArray.isEmpty())
				value[i]=relayArray.remove(0)==RelayState.CLOSED;
		
		int newValueMasks[]=getValueMasks(value);
		
		ports[0].actualConfiguration.valueMask=(short) newValueMasks[0];
		ports[1].actualConfiguration.valueMask=(short) newValueMasks[1];
	}

	public static boolean[] createBooleanArray(int a, int b){
		boolean result[] = new boolean[16];
		
		for(int i=0;i<8;i++){
			result[i] = a%2==1;
			a=a>>1;
		}
		for(int i=8;i<16;i++){
			result[i] = b%2==1;
			b=b>>1;
		}
		
		return result;
	}
	
	public static int[] getValueMasks(boolean[] value) {
		int result[] = {0,0};
		
		for(int i = 7;i>=0; i--){
			result[0]=result[0]<<1;
			result[0]+=value[i]?1:0;
		}
		
		for(int i = 15;i>=8; i--){
			result[1]=result[1]<<1;
			result[1]+=value[i]?1:0;
		}
		
		return result;
	}

}
