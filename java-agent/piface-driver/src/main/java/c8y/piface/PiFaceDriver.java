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

package c8y.piface;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Hardware;
import c8y.Relay;
import c8y.Relay.RelayState;
import c8y.lx.driver.DeviceManagedObject;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.pi4j.component.switches.SwitchListener;
import com.pi4j.component.switches.SwitchState;
import com.pi4j.component.switches.SwitchStateChangeEvent;
import com.pi4j.device.piface.impl.PiFaceDevice;
import com.pi4j.wiringpi.Spi;

/**
 * Elementary support for the switches and LEDs on the PiFace. Currently, only a
 * single PiFace on Channel 0 is supported.
 */
public class PiFaceDriver implements Driver, OperationExecutor {

    private static Logger logger = LoggerFactory.getLogger(PiFaceDriver.class);

    private static final int NUMBUTTONS = 4;
    private static final int NUMLEDS = 8;
    public static final String LEDTYPE = "c8y_PiFaceLED";
    public static final String SWITCHTYPE = "c8y_PiFaceSwitch";
	public static final String XTIDTYPE = "c8y_Serial";

	private Platform platform;
	private DeviceManagedObject dmo;
	private PiFaceDevice piface;
	
	private ManagedObjectRepresentation[] buttonMos = new ManagedObjectRepresentation[NUMBUTTONS];
	private ManagedObjectRepresentation[] ledMos = new ManagedObjectRepresentation[NUMLEDS];
	private EventRepresentation eventTemplate;

	@Override
	public void initialize() throws Exception {
		piface = new PiFaceDevice(Spi.CHANNEL_0);
	}
	
	@Override
	public void initialize(Platform platform) throws Exception {
		this.platform = platform;
		dmo = new DeviceManagedObject(platform);		

		eventTemplate = new EventRepresentation();
		eventTemplate.setText("Button pressed");
		eventTemplate.setType("c8y_ButtonPressedEvent");
	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[] { this };
	}
	
	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		// Nothing to do here, all items are added as children.
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation mo) {
		String piSerial = mo.get(Hardware.class).getSerialNumber();
		String idPrefix = "raspberrypi-" + piSerial + "-";
		String namePrefix = "RaspPi " + piSerial.substring(8) + " ";

		try {
			createLeds(mo.getId(), idPrefix, namePrefix);
			createButtons(mo.getId(), idPrefix, namePrefix);
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createLeds(GId parent, String idPrefix, String namePrefix) {
		for (int idx = 0; idx < NUMLEDS; idx++) {
			Relay relay = new Relay();

			if (piface.getLed(idx).isOn()) {
				relay.setRelayState(RelayState.CLOSED);
			} else {
				relay.setRelayState(RelayState.OPEN);
			}

			ledMos[idx] = new ManagedObjectRepresentation();
			ledMos[idx].setType(LEDTYPE);
			ledMos[idx].setName(namePrefix + "LED " + idx);
			ledMos[idx].set(relay);
			OpsUtil.addSupportedOperation(ledMos[idx], supportedOperationType());

			String id = idPrefix + "led-" + idx;

			logger.trace("Creating LED {}", idx);
			createOrUpdate(ledMos[idx], id, parent);
		}
	}

	private void createButtons(GId parent, String idPrefix, String namePrefix) {
		for (int idx = 0; idx < NUMBUTTONS; idx++) {
			buttonMos[idx] = new ManagedObjectRepresentation();
			buttonMos[idx].setType(SWITCHTYPE);
			buttonMos[idx].setName(namePrefix + "Button " + idx);
			
			String id = idPrefix + "button-" + idx;

			logger.trace("Creating button {}", idx);
			createOrUpdate(buttonMos[idx], id, parent);
		}
	}

	private boolean createOrUpdate(ManagedObjectRepresentation mo, String id, GId parent) {
		ID extId = new ID(id);
		extId.setType(XTIDTYPE);
		return dmo.createOrUpdate(mo, extId, parent);
	}

	@Override
	public void start() {
		for (int idx = 0; idx < NUMBUTTONS; idx++) {
			piface.getSwitch(idx).addListener(new EventSwitchListener(idx));
		}
	}

	class EventSwitchListener implements SwitchListener {

        private int idx;

		public EventSwitchListener(int idx) {
			this.idx = idx;
		}

		@Override
		public void onStateChange(SwitchStateChangeEvent event) {
			if (event.getNewState() == SwitchState.ON) {
				logger.trace("Button {} pressed, sending event", idx);
				eventTemplate.setSource(buttonMos[idx]);
				eventTemplate.setTime(new Date());
				try {
					platform.getEventApi().create(eventTemplate);
				} catch (SDKException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String supportedOperationType() {
		return "c8y_Relay";
	}

	@Override
	public void execute(OperationRepresentation operation, boolean cleanup) throws Exception {
		Relay relayOp = operation.get(Relay.class);
		GId targetGId = operation.getDeviceId();

		for (int idx = 0; idx < NUMLEDS; idx++) {
			if (ledMos[idx].getId().equals(targetGId)) {
				logger.trace("Switching relay {} to {}", idx,
						relayOp.getRelayState());
				if (relayOp.getRelayState() == RelayState.CLOSED) {
					piface.getLed(idx).on();
				} else {
					piface.getLed(idx).off();
				}
				ManagedObjectRepresentation updateMo = new ManagedObjectRepresentation();
				updateMo.setId(ledMos[idx].getId());
				updateMo.set(relayOp);
				platform.getInventoryApi().update(updateMo);
				ledMos[idx].set(relayOp);
				operation.setStatus(OperationStatus.SUCCESSFUL.toString());
				break;
			}
		}
	}
}
