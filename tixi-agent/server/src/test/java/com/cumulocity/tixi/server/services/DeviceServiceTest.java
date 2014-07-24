package com.cumulocity.tixi.server.services;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.*;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.context.DeviceCredentials;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.cumulocity.tixi.server.model.SerialNumber;

public class DeviceServiceTest {

    private DeviceContextService contextService = mock(DeviceContextService.class);
    private InventoryRepository inventoryRepository = mock(InventoryRepository.class);
    private DeviceService deviceService = new DeviceService(mock(DeviceCredentialsApi.class), contextService, inventoryRepository);

    GId parentId = GId.asGId("1");

    @Test
    public void shouldSaveTixiAgent() throws Exception {
        ArgumentCaptor<ManagedObjectRepresentation> captor = ArgumentCaptor.forClass(ManagedObjectRepresentation.class);
        SerialNumber serialNumber = new SerialNumber("12345");
        
        deviceService.saveTixiAgent(serialNumber);
        
        verify(inventoryRepository).save(captor.capture(), eq(serialNumber));
        ManagedObjectRepresentation created = captor.getValue();
        assertThat(created.getName()).isEqualTo("c8y_TixiAgent_12345");
        assertThat(created.getAttrs().containsKey("com_cumulocity_model_Agent")).isEqualTo(true);
        assertThat(created.getAttrs().containsKey("c8y_IsDevice")).isEqualTo(true);
        assertThat(created.getAttrs().containsKey("c8y_RequiredAvailability")).isEqualTo(true);
        assertThat(created.getAttrs().containsKey("c8y_SupportedOperations")).isEqualTo(true);
    }
    
    @Test
    public void shouldSaveDevice() throws Exception {
        ArgumentCaptor<ManagedObjectRepresentation> captor = ArgumentCaptor.forClass(ManagedObjectRepresentation.class);
        GId tixiAgentId = GId.asGId("12345");
        when(contextService.getCredentials()).thenReturn(new DeviceCredentials(null, null, null, null, tixiAgentId));
        SerialNumber serialNumber = new SerialNumber("DHZ_0_" + tixiAgentId.getValue());
        when(inventoryRepository.save(captor.capture(), eq(serialNumber))).thenReturn(new ManagedObjectRepresentation());
        
        deviceService.saveDeviceIfNotExists("DHZ_0", "DHZ_0", parentId);
        
        ManagedObjectRepresentation created = captor.getValue();
        assertThat(created.getName()).isEqualTo("DHZ_0");
        verify(inventoryRepository).bindToParent(eq(parentId), any(GId.class));
        assertThat(created.getAttrs().containsKey("c8y_IsDevice")).isEqualTo(false);
    }
    
    @Test
    public void shouldSaveAgent() throws Exception {
        ArgumentCaptor<ManagedObjectRepresentation> captor = ArgumentCaptor.forClass(ManagedObjectRepresentation.class);
        GId tixiAgentId = GId.asGId("12345");
        when(contextService.getCredentials()).thenReturn(new DeviceCredentials(null, null, null, null, tixiAgentId));
        SerialNumber serialNumber = new SerialNumber("M-BUS_" + tixiAgentId.getValue());
        when(inventoryRepository.save(captor.capture(), eq(serialNumber))).thenReturn(new ManagedObjectRepresentation());
        
        deviceService.saveAgentIfNotExists("M-BUS", "M-BUS");
        
        ManagedObjectRepresentation created = captor.getValue();
        assertThat(created.getName()).isEqualTo("M-BUS");
        verify(inventoryRepository).bindToParent(eq(tixiAgentId), any(GId.class));
        assertThat(created.getAttrs().containsKey("com_cumulocity_model_Agent")).isEqualTo(true);
        assertThat(created.getAttrs().containsKey("c8y_IsDevice")).isEqualTo(true);
    }
}
