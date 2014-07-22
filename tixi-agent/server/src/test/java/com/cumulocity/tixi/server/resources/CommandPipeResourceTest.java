package com.cumulocity.tixi.server.resources;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.cumulocity.tixi.server.model.TixiRequestType;
import com.cumulocity.tixi.server.services.ChunkedOutputMessageChannel;
import com.cumulocity.tixi.server.services.DeviceMessageChannelService;

public class CommandPipeResourceTest {

    DeviceMessageChannelService device = mock(DeviceMessageChannelService.class);

    CommandPipeResource commandPipe = new CommandPipeResource(device);

    @Test
    public void shouldBootstrap() {
        ArgumentCaptor<TixiRequestType> reqTypeCaptor = ArgumentCaptor.forClass(TixiRequestType.class);

        commandPipe.open("some_serial", "some_user");

        verify(device).registerMessageOutput(Mockito.any(ChunkedOutputMessageChannel.class));
    }

}
