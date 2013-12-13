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

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.devicecontrol.OperationFilter;

/**
 * Polls the platform for pending operations, executes the operations and
 * reports back the status. Operations can only be executed on devices that are
 * currently connected to the agent. Operations for devices that are currently
 * not connected are left in the queue on the platform for retry.
 */
public class OperationDispatcher extends TimerTask {
	public static final long POLLING_DELAY = 5000;
	public static final long POLLING_INTERVAL = 5000;

	/**
	 * @param platform
	 *            The connection to the platform.
	 * @param agent
	 *            The ID of this agent.
	 * @param cfgDriver
	 * @param executers
	 *            A map of currently connected devices. The map is maintained by
	 *            the threads communicating with the devices, hence it needs to
	 *            be thread-safe.
	 */
	public OperationDispatcher(Platform platform, GId agent)
			throws SDKException {
		this.operations = platform.getDeviceControlApi();
		this.agent = agent;

		finishExecutingOps();
		pollPendingOps();
	}

	public void finish(OperationRepresentation operation) throws SDKException {
		operation.setStatus(OperationStatus.SUCCESSFUL.toString());
		operations.update(operation);
	}

	public void fail(OperationRepresentation operation, String text,
			SDKException x) throws SDKException {
		operation.setStatus(OperationStatus.FAILED.toString());
		operation.setFailureReason(text + " " + x.getMessage());
		operations.update(operation);
	}

	/**
	 * Clean up operations that are stuck in "executing" state.
	 */
	private void finishExecutingOps() throws SDKException {
		logger.debug("Cancelling hanging operations");
		for (OperationRepresentation operation : byStatus(OperationStatus.EXECUTING)) {
			operation.setStatus(OperationStatus.FAILED.toString());
			operations.update(operation);
		}
	}

	private void pollPendingOps() {
		Timer poller = new Timer("OperationPoller");
		poller.scheduleAtFixedRate(this, POLLING_DELAY, POLLING_INTERVAL);
	}

	@Override
	public void run() {
		logger.debug("Executing queued operations");
		try {
			executePendingOps();
		} catch (Exception x) {
			logger.warn("Error while executing operations", x);
		}
	}

	private void executePendingOps() throws SDKException {
		for (OperationRepresentation operation : byStatus(OperationStatus.PENDING)) {
			GId gid = operation.getDeviceId();

			TrackerDevice device = ManagedObjectCache.instance().get(gid);
			if (device == null) {
				continue; // Device hasn't been identified yet
			}

			Executor exec = ConnectionRegistry.instance().get(device.getImei());

			if (exec != null) {
				// Device is currently connected, execute on device
				executeOperation(exec, operation);
				if (OperationStatus.FAILED.toString().equals(
						operation.getStatus())) {
					// Connection error, remove device
					ConnectionRegistry.instance().remove(device.getImei());
				}
			}
		}
	}

	private void executeOperation(Executor exec,
			OperationRepresentation operation) throws SDKException {
		operation.setStatus(OperationStatus.EXECUTING.toString());
		operations.update(operation);

		try {
			exec.execute(operation);
		} catch (Exception x) {
			String msg = "Error during communication with device "
					+ operation.getDeviceId();
			logger.warn(msg, x);
			operation.setStatus(OperationStatus.FAILED.toString());
			operation.setFailureReason(msg + x.getMessage());
		}
		operations.update(operation);
	}

	private Iterable<OperationRepresentation> byStatus(OperationStatus status)
			throws SDKException {
		OperationFilter opsFilter = new OperationFilter().byAgent(
				agent.getValue()).byStatus(status);
		return operations.getOperationsByFilter(opsFilter).get().allPages();
	}

	private static Logger logger = LoggerFactory
			.getLogger(OperationDispatcher.class);

	private DeviceControlApi operations;
	private GId agent;
}
