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

package c8y.pi.agent;

import java.util.List;
import java.util.Map;

import c8y.pi.driver.Executer;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationCollectionRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.PagedCollectionResource;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.devicecontrol.OperationFilter;
import com.cumulocity.sdk.client.notification.Subscriber;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;

/**
 * The operation dispatcher manages the life cycle of operations send from the
 * platform to the agent. For this purpose, it queries the executing and pending
 * operations targeted to the agent and dispatches them to a driver that
 * understands the operation.
 */
public class OperationDispatcher {

	public OperationDispatcher(Platform platform, GId gid,
			Map<String, Executer> dispatchMap) throws SDKException {
		this.control = platform.getDeviceControlApi();
		this.gid = gid;
		this.dispatchMap = dispatchMap;

		finishExecutingOps();
		listenToOperations();
		executePendingOps();
	}

	private void finishExecutingOps() throws SDKException {
		for (OperationRepresentation operation : byStatus(OperationStatus.EXECUTING)) {
			execute(operation);
		}
	}

	private void listenToOperations() throws SDKException {
		Subscriber<GId, OperationRepresentation> subscriber;
		subscriber = control.getNotificationsSubscriber();
		subscriber.subscribe(gid,
				new SubscriptionListener<GId, OperationRepresentation>() {
					@Override
					public void onError(Subscription<GId> sub, Throwable e) {
						e.printStackTrace();
					}

					@Override
					public void onNotification(Subscription<GId> sub,
							OperationRepresentation operation) {
						try {
							execute(operation);
						} catch (SDKException e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void executePendingOps() throws SDKException {
		for (OperationRepresentation operation : byStatus(OperationStatus.PENDING)) {
			executePending(operation);
		}
	}

	private List<OperationRepresentation> byStatus(OperationStatus status)
			throws SDKException {
		OperationFilter opsFilter = new OperationFilter().byAgent(
				gid.getValue()).byStatus(status);
		PagedCollectionResource<OperationCollectionRepresentation> opsQuery = control
				.getOperationsByFilter(opsFilter);
		// TODO Fix this, use an iterable
		OperationCollectionRepresentation ops = opsQuery.get(1000);
		return ops.getOperations();
	}

	private void executePending(OperationRepresentation operation)
			throws SDKException {
		operation.setStatus(OperationStatus.EXECUTING.toString());
		control.update(operation);

		execute(operation);
	}

	private void execute(OperationRepresentation operation) throws SDKException {
		try {
			for (String key : operation.getAttrs().keySet()) {
				if (dispatchMap.containsKey(key)) {
					dispatchMap.get(key).execute(operation);
				}
			}
		} catch (Exception e) {
			operation.setStatus(OperationStatus.FAILED.toString());
			operation.setFailureReason(ErrorLog.toString(e));
		}
		control.update(operation);
	}

	private DeviceControlApi control;
	private GId gid;
	private Map<String, Executer> dispatchMap;
}
