package com.cumulocity.tixi.server.services;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.cumulocity.agent.server.repository.DeviceControlRepository;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.devicecontrol.OperationFilter;
import com.cumulocity.tixi.server.model.Operations;
import com.cumulocity.tixi.server.services.handler.LogDefinitionRegister;

public class DeviceControlServiceTest {

    DeviceControlRepository repository = mock(DeviceControlRepository.class);
    DeviceMessageChannelService deviceMessageChannelService = mock(DeviceMessageChannelService.class);
    TixiRequestFactory requestFactory = mock(TixiRequestFactory.class);
    LogDefinitionRegister logDefinitionRegister = mock(LogDefinitionRegister.class);
    DeviceControlService deviceControlService = new DeviceControlService(repository, deviceMessageChannelService, requestFactory,
            logDefinitionRegister);

    GId tixiAgentId = GId.asGId("123456");

    ArgumentCaptor<OperationRepresentation> captor = ArgumentCaptor.forClass(OperationRepresentation.class);
    
    @Before
    public void setup() {
        when(repository.findAllByFilter(any(OperationFilter.class))).thenReturn(asList(Operations.asOperation(GId.asGId("1"))));
    }

    @Test
    public void shouldMarkAllOperationsSuccess() throws Exception {
        deviceControlService.markAllOperationsSuccess(tixiAgentId);

        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(OperationStatus.SUCCESSFUL.name());
    }

    @Test
    public void shouldMarkAllOperationsFailed() throws Exception {
        deviceControlService.markAllOperationsFailed(tixiAgentId);

        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(OperationStatus.FAILED.name());
    }
}
