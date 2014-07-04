package c8y.tinkerforge.bricklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.tinkerforge.BrickletSegmentDisplay4x7;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import c8y.lx.driver.DeviceManagedObject;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;
import c8y.tinkerforge.TFIds;

public class SegmentDisplay4x7 implements Driver{
	
	private static final String SET_SEGMENTS_OP_TYPE="c8y_Set_Segments";
	private static final String TYPE="4x7SegmentDisplay";
	
	private static final Logger logger = LoggerFactory
			.getLogger(SegmentDisplay4x7.class);

	private BrickletSegmentDisplay4x7 display;
	private ManagedObjectRepresentation displayMo = new ManagedObjectRepresentation();
	private Platform platform;
	private String id;
	
	public SegmentDisplay4x7(String id, BrickletSegmentDisplay4x7 display) {
		this.id=id;
		this.display=display;
	}
	
	@Override
	public void initialize() throws Exception {
		// Nothing to be done.
	}

	@Override
	public void initialize(Platform platform) throws Exception {
		this.platform=platform;
	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[]{};
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		// Nothing to be done.
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation parent) {
		try {
			displayMo.set(TFIds.getHardware(display, TYPE));
		} catch (TimeoutException | NotConnectedException e) {
			logger.warn("Cannot read hardware parameters", e);
		}
		
		displayMo.setType(TFIds.getType(TYPE));
		displayMo.setName(TFIds.getDefaultName(parent.getName(), TYPE, id));
		
		for(OperationExecutor operation:getSupportedOperations())
			OpsUtil.addSupportedOperation(displayMo, operation.supportedOperationType());
		
		try {
			DeviceManagedObject dmo = new DeviceManagedObject(platform);
			dmo.createOrUpdate(displayMo, TFIds.getXtId(id), parent.getId());
		} catch (SDKException e) {
			logger.warn("Cannot create remote switch object", e);
		}
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}
	
	class SetSegmentsOperationExecutor implements OperationExecutor{

		@Override
		public String supportedOperationType() {
			return SET_SEGMENTS_OP_TYPE;
		}

		@Override
		public void execute(OperationRepresentation operation, boolean cleanup)
				throws Exception {
			if (cleanup)
				operation.setStatus(OperationStatus.FAILED.toString());
			
			display.setSegments(new short[]{(short)operation.getProperty("segment1"),
											(short)operation.getProperty("segment2"),
											(short)operation.getProperty("segment3"),
											(short)operation.getProperty("segment4"),}, 
					(short)operation.getProperty("brightness"), 
					(boolean)operation.getProperty("colon"));
			
			operation.setStatus(OperationStatus.SUCCESSFUL.toString());
		}
		
	}

}
