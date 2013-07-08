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

package c8y.trackeragent;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;

/**
 * Maintains agent entry in the inventory and currently connected devices.
 */
public class TrackerAgent extends DeviceManagedObject {
	public TrackerAgent(Platform platform) throws SDKException {
		super(platform);
		this.platform = platform;

		createAgentMo();
		new OperationDispatcher(platform, agentGid);
	}

	/**
	 * Get an existing or create a new device. Caches the devices used so far
	 * during the life of the agent.
	 * 
	 * @param imei
	 *            The IMEI identifying the device.
	 * @param exec
	 *            The executor responsible for running operations on the device.
	 * @throws SDKException 
	 */
	public TrackerDevice getOrCreate(String imei) throws SDKException {
		TrackerDevice device = ManagedObjectCache.instance().get(imei);

		if (device == null) {
			device = new TrackerDevice(platform, agentGid, imei);
			ManagedObjectCache.instance().put(device);
		}

		return device;
	}

	private void createAgentMo() throws SDKException {
		ManagedObjectRepresentation agentMo = new ManagedObjectRepresentation();
		agentMo.setType("c8y_TrackerAgent");
		agentMo.setName("Tracker agent");

		ID extId = new ID("c8y_TrackerAgent");
		extId.setType("c8y_ServerSideAgent");

		createOrUpdate(agentMo, extId, null);
		agentGid = agentMo.getId();
	}

	private Platform platform;
	private GId agentGid;
}
