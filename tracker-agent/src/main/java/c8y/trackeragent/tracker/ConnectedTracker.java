/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.tracker;

import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.server.ConnectionDetails;

public interface ConnectedTracker {
    
    void executeOperation(OperationContext operation) throws Exception;

    void executeReports(ConnectionDetails connectionDetails, byte[] reports);
    
    TrackingProtocol getTrackingProtocol();

    String translateOperation(OperationContext operationCtx) throws Exception;
    
}
