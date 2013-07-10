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

package com.cumulocity.sdk.agent.action;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationCollectionRepresentation;
import com.cumulocity.sdk.agent.model.DevicesManagingAgent;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.devicecontrol.OperationFilter;

/**
 * Obtains from the platform operations to be executed on devices controlled be an agent.
 */
public class ObtainDeviceOperationsAction implements AgentAction {

    private Platform platform;

    private DevicesManagingAgent<?> agent;

    @Autowired
    public ObtainDeviceOperationsAction(Platform platform, DevicesManagingAgent<?> agent) {
        this.platform = platform;
        this.agent = agent;
    }

    @Override
    public void run() {
        try {
            String agentId = agent.getGlobalId().getValue();
            OperationStatus status = OperationStatus.PENDING;
            OperationFilter filter = new OperationFilter().byAgent(agentId).byStatus(status);
            OperationCollectionRepresentation collection = platform.getDeviceControlApi().getOperationsByFilter(filter).get();
            agent.getOperationsQueue().addAll(collection.getOperations());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
