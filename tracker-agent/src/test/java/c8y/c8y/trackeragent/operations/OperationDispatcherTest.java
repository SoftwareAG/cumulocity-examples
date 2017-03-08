package c8y.c8y.trackeragent.operations;

import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.operations.OperationDispatcher;
import c8y.trackeragent.operations.OperationExecutor;
import c8y.trackeragent.service.TrackerDeviceContextService;
import com.cumulocity.sdk.client.SDKException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.cumulocity.model.operation.OperationStatus.PENDING;
import static org.mockito.Answers.RETURNS_MOCKS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OperationDispatcherTest {

    @Mock(answer = RETURNS_MOCKS)
    private TrackerDeviceContextService contextService;

    @Mock(answer = RETURNS_MOCKS)
    private DeviceCredentials deviceCredentials;

    @Mock(answer = RETURNS_MOCKS)
    private OperationExecutor operationExecutor;

    @Mock(answer = RETURNS_MOCKS)
    private ScheduledExecutorService scheduledExecutorService;

    @InjectMocks
    private OperationDispatcher operationDispatcher;

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
