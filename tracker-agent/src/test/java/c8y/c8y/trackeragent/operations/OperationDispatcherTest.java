/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.c8y.trackeragent.operations;

import c8y.TrackerDeviceContextServiceMock;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.operations.OperationDispatcher;
import c8y.trackeragent.operations.OperationExecutor;
import com.cumulocity.sdk.client.SDKException;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.cumulocity.model.operation.OperationStatus.PENDING;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OperationDispatcherTest {

    private final DeviceCredentials deviceCredentials = Mockito.mock(DeviceCredentials.class);
    private final OperationExecutor operationExecutor = Mockito.mock(OperationExecutor.class);
    private final ScheduledExecutorService scheduledExecutorService = Mockito.mock(ScheduledExecutorService.class);

    private final OperationDispatcher operationDispatcher = new OperationDispatcher(
            deviceCredentials, new TrackerDeviceContextServiceMock(), operationExecutor
    );

    @Test
    public void shouldCancelFutureWhenTenantIsDisabled() {
        final ScheduledFuture future = mock(ScheduledFuture.class);
        when(operationExecutor.getOperationsByStatusAndAgent(PENDING)).thenThrow(new RuntimeException(new SDKException(401, "")));
        when(scheduledExecutorService.scheduleWithFixedDelay(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(future);

        operationDispatcher.startPolling(scheduledExecutorService);
        operationDispatcher.run();

        verify(future).cancel(false);
    }
}
