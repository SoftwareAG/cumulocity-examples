package c8y.trackeragent.device;

import com.cumulocity.model.ID;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.identity.IdentityApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TrackerDeviceTest {

    @Mock
    private IdentityApi identityApi;

    @InjectMocks
    private TrackerDevice trackerDevice;

    @Test
    public void shouldNotRethrowExceptionWhenNotFound() {
        when(identityApi.getExternalId(any(ID.class))).thenThrow(new RuntimeException(new SDKException(404, "")));

        trackerDevice.tryGetBinding(mock(ID.class));

//        no exception expected
    }

    @Test(expected = RuntimeException.class)
    public void shouldRethrowExceptionWhenNotFound() {
        when(identityApi.getExternalId(any(ID.class))).thenThrow(new RuntimeException(new SDKException(402, "")));

        trackerDevice.tryGetBinding(mock(ID.class));
    }
}
