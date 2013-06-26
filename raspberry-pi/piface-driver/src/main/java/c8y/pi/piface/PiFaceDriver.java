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

package c8y.pi.piface;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Hardware;
import c8y.pi.driver.DeviceManagedObject;
import c8y.pi.driver.Driver;
import c8y.pi.driver.Executer;

import com.cumulocity.model.ID;
import com.cumulocity.model.control.Relay;
import com.cumulocity.model.control.Relay.RelayState;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
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
public class PiFaceDriver implements Driver, Executer {
	private static final int NUMBUTTONS = 4;
	private static final int NUMLEDS = 8;
	public static final String LEDTYPE = "c8y_PiFaceLED";
	public static final String SWITCHTYPE = "c8y_PiFaceSwitch";
	public static final String XTIDTYPE = "c8y_Serial";

	@Override
	public void initialize(Platform platform) throws Exception {
		this.platform = platform;
		piface = new PiFaceDevice(Spi.CHANNEL_0);

		eventTemplate = new EventRepresentation();
		eventTemplate.setText("Button pressed");
		eventTemplate.setType("c8y_ButtonPressedEvent");
	}

	@Override
	public Executer[] getSupportedOperations() {
		return new Executer[] { this };
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

	private void createLeds(GId parent, String idPrefix, String namePrefix)
			throws SDKException {
		for (int idx = 0; idx < NUMLEDS; idx++) {
			Relay relay = new Relay();

			if (piface.getLed(idx).isOn()) {
				relay.setRelayState(RelayState.CLOSED);
			} else {
				relay.setRelayState(RelayState.OPEN);
			}

			ledMos[idx] = new ManagedObjectRepresentation();
			ledMos[idx].set(relay);

			String id = idPrefix + "led-" + idx;
			String defaultName = namePrefix + "LED " + idx;

			logger.trace("Creating LED {}", idx);
			boolean created = createOrUpdate(ledMos[idx], id, defaultName,
					LEDTYPE);
			if (created) {
				link(parent, ledMos[idx]);
			}
		}
	}

	private void createButtons(GId parent, String idPrefix, String namePrefix)
			throws SDKException {
		for (int idx = 0; idx < NUMBUTTONS; idx++) {
			buttonMos[idx] = new ManagedObjectRepresentation();
			String id = idPrefix + "button-" + idx;
			String defaultName = namePrefix + "Button " + idx;

			logger.trace("Creating button {}", idx);
			boolean created = createOrUpdate(buttonMos[idx], id, defaultName,
					SWITCHTYPE);
			if (created) {
				link(parent, buttonMos[idx]);
			}
		}
	}

	private boolean createOrUpdate(ManagedObjectRepresentation mo, String id,
			String defaultName, String type) throws SDKException {
		ID extId = new ID(id);
		extId.setType(XTIDTYPE);
		DeviceManagedObject dmo = new DeviceManagedObject(platform, extId);
		return dmo.createOrUpdate(mo, defaultName, type);
	}

	private void link(GId parent, ManagedObjectRepresentation child)
			throws SDKException {
		ManagedObjectReferenceRepresentation moRef = new ManagedObjectReferenceRepresentation();
		moRef.setManagedObject(child);
		platform.getInventoryApi().getManagedObject(parent)
				.addChildDevice(moRef);
	}

	@Override
	public void start() {
		for (int idx = 0; idx < NUMBUTTONS; idx++) {
			piface.getSwitch(idx).addListener(new EventSwitchListener(idx));
		}
	}

	class EventSwitchListener implements SwitchListener {
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

		private int idx;
	};

	@Override
	public String supportedOperationType() {
		return "c8y_Relay";
	}

	@Override
	public void execute(OperationRepresentation operation, boolean cleanup) throws Exception {
		Relay relayOp = operation.get(Relay.class);
		GId targetGId = operation.getId();

		for (int idx = 0; idx < NUMLEDS; idx++) {
			if (ledMos[idx].getId().equals(targetGId)) {
				logger.trace("Switching relay {} to {}", idx, relayOp.getRelayState());
				if (relayOp.getRelayState() == RelayState.CLOSED) {
					piface.getLed(idx).on();
				} else {
					piface.getLed(idx).off();
				}
				ledMos[idx].set(relayOp);
				platform.getInventoryApi().getManagedObject(targetGId)
						.update(ledMos[idx]);
				operation.setStatus(OperationStatus.SUCCESSFUL.toString());
				break;
			}
		}
	}

	private static Logger logger = LoggerFactory.getLogger(PiFaceDriver.class);
	
	private Platform platform;
	private ManagedObjectRepresentation[] buttonMos = new ManagedObjectRepresentation[NUMBUTTONS];
	private ManagedObjectRepresentation[] ledMos = new ManagedObjectRepresentation[NUMLEDS];
	private EventRepresentation eventTemplate;

	private PiFaceDevice piface;
}
