/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.operations;

import com.cumulocity.agent.server.logging.LoggingService;
import com.cumulocity.agent.server.repository.IdentityRepository;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;

import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.server.ActiveConnection;
import c8y.trackeragent.server.ConnectionsContainer;
import c8y.trackeragent.server.TestConnectionDetails;
import c8y.trackeragent.tracker.BaseTracker;
import c8y.trackeragent.tracker.ConnectedTracker;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

public class OperationExecutorTest {

    private final DeviceControlApi deviceControlApi = mock(DeviceControlApi.class);
    private final LoggingService loggingService = mock(LoggingService.class);
    private final IdentityRepository identityRepository = mock(IdentityRepository.class);
    private final ConnectionsContainer connectionsContainer = mock(ConnectionsContainer.class);
    private final OperationSmsDelivery operationSmsDelivery = mock(OperationSmsDelivery.class);
    private final BaseTracker baseTracker = mock(BaseTracker.class);
        
    private OperationExecutor operationExecutor;
    private OperationRepresentation operation = mock(OperationRepresentation.class);
    private ConnectedTracker connectedTracker = mock(ConnectedTracker.class);
    private TrackerDevice device = mock(TrackerDevice.class);
    private ActiveConnection activeConnection = mock(ActiveConnection.class);
    private String imei = "1234";
    private String tenant = "tenant";
    private GId deviceId = new GId("2");
    private String translation = "text-to-device";
    
    @Before
    public void setup() {
        when(operation.getId()).thenReturn(new GId("1"));
        when(operation.getDeviceId()).thenReturn(deviceId);
        when(device.getImei()).thenReturn(imei);
        operationExecutor = new OperationExecutor(deviceControlApi, loggingService, identityRepository, connectionsContainer, operationSmsDelivery, baseTracker);
    }
    
    public void setupForSmsOperation() {
        when(device.getTrackingProtocolInfo()).thenReturn(TestConnectionDetails.DEFAULT_PROTOCOL);
        when(operationSmsDelivery.isSmsMode(operation)).thenReturn(true);
        try {
            when(connectedTracker.translateOperation(any(OperationContext.class))).thenReturn(translation);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void shouldExecuteSmsWhenConnectionNotExist() {
        setupForSmsOperation();
        when(connectionsContainer.get(anyString())).thenReturn(null);
        when(baseTracker.getTrackerForTrackingProtocol(any(TrackingProtocol.class))).thenReturn(connectedTracker);

        operationExecutor.execute(operation, device);
        verify(device).getTrackingProtocolInfo();
        verify(baseTracker).getTrackerForTrackingProtocol(TestConnectionDetails.DEFAULT_PROTOCOL);
        verify(operationSmsDelivery).deliverSms(translation, deviceId, imei);
        
    }
    
    @Test
    public void shouldExecuteSmsWhenConnectionExist() {
        setupForSmsOperation();
        when(connectionsContainer.get(anyString())).thenReturn(activeConnection);
        when(activeConnection.getConnectedTracker()).thenReturn(connectedTracker);
        
        operationExecutor.execute(operation, device);
        verify(device, never()).getTrackingProtocolInfo();
        verify(baseTracker, never()).getTrackerForTrackingProtocol(TestConnectionDetails.DEFAULT_PROTOCOL);
        verify(operationSmsDelivery).deliverSms(translation, deviceId, imei);
        
    }
    
    
    
}
