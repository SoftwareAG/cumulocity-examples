package com.cumulocity.tixi.server.resources;

import static com.cumulocity.tixi.server.resources.TixiRequest.statusOK;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.cumulocity.tixi.server.model.TixiRequestType;
import com.cumulocity.tixi.server.request.util.Device;
import com.cumulocity.tixi.server.services.DeviceControlService;
import com.cumulocity.tixi.server.services.MessageChannel;
import com.cumulocity.tixi.server.services.TixiRequestFactory;

public class CommandPipeResourceTest {

    Device device = mock(Device.class);
    DeviceControlService deviceControlService = mock(DeviceControlService.class);
    TixiRequestFactory tixiRequestFactory = mock(TixiRequestFactory.class);
    CommandPipeResource commandPipe = new CommandPipeResource(device, deviceControlService, tixiRequestFactory);

	@Test
    public void shouldBootstrap() {
        ArgumentCaptor<TixiRequestType> reqTypeCaptor = ArgumentCaptor.forClass(TixiRequestType.class);
        
        commandPipe.open("some_serial", "some_user");
        
        verify(device, times(2)).put(reqTypeCaptor.capture());
        assertThat(reqTypeCaptor.getAllValues()).containsExactly(TixiRequestType.EXTERNAL_DATABASE, TixiRequestType.LOG_DEFINITION);
        verify(device).put(statusOK());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldSubscribeOnOperations() {
    	commandPipe.open("some_serial", "some_user");

    	verify(deviceControlService).subscirbe(any(MessageChannel.class));
    }
    
}
