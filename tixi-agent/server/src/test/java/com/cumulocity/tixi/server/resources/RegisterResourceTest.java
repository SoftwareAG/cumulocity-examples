package com.cumulocity.tixi.server.resources;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.mockito.Mockito;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.TixiDeviceCredentails;
import com.cumulocity.tixi.server.services.DeviceControlService;
import com.cumulocity.tixi.server.services.DeviceService;

public class RegisterResourceTest {

    @Test
    public void shouldBootstrap() {
        DeviceService deviceService = mock(DeviceService.class);
        DeviceControlService deviceControlService = mock(DeviceControlService.class);
        RegisterResource resource = new RegisterResource(deviceService,deviceControlService);
        when(deviceService.register(new SerialNumber("12345"))).thenReturn(new TixiDeviceCredentails("user", "pass", "id"));
        
        Response response = resource.get("12345", null);
        
        TixiRequest tixiResponse = (TixiRequest) response.getEntity();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
        assertThat(tixiResponse.getProperties().get("user")).isEqualTo("user");
        verify(deviceControlService).startOperationExecutor(GId.asGId("id"));
    }

}
