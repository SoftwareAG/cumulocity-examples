package com.cumulocity.tixi.server.resources;

import static com.cumulocity.tixi.server.resources.TixiJsonResponse.statusOKJson;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.cumulocity.tixi.server.model.RequestType;
import com.cumulocity.tixi.server.request.util.Device;

public class OpenChannelResourceTest {

    @Test
    public void shouldBootstrap() {
        ArgumentCaptor<RequestType> reqTypeCaptor = ArgumentCaptor.forClass(RequestType.class);
        Device device = mock(Device.class);
        OpenChannelResource resource = new OpenChannelResource(null, device);
        
        resource.open("some_serial", "some_user");
        
        verify(device, times(2)).put(reqTypeCaptor.capture());
        assertThat(reqTypeCaptor.getAllValues()).containsExactly(RequestType.EXTERNAL_DATABASE, RequestType.LOG_DEFINITION);
        verify(device).put(statusOKJson());
    }

}
