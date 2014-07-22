package com.cumulocity.tixi.server.services.handler;

import static com.cumulocity.model.idtype.GId.asGId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;

import com.cumulocity.agent.server.context.DeviceContext;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.context.DeviceCredentials;
import com.cumulocity.agent.server.repository.DeviceControlRepository;
import com.cumulocity.agent.server.repository.IdentityRepository;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.measurement.MeasurementApi;

public abstract class BaseTixiHandlerTest {
	
	protected DeviceContextService deviceContextService = mock(DeviceContextService.class);
	protected IdentityRepository identityRepository = mock(IdentityRepository.class);
	protected InventoryRepository inventoryRepository = mock(InventoryRepository.class);
	protected MeasurementApi measurementApi = mock(MeasurementApi.class);
	protected LogDefinitionRegister logDefinitionRegister = mock(LogDefinitionRegister.class);
	protected DeviceControlRepository deviceControlRepository = mock(DeviceControlRepository.class);
	protected ManagedObjectRepresentation agentRep;
	
	@Before
	public void init() throws Exception {
		agentRep = new ManagedObjectRepresentation();
		agentRep.setId(asGId("agentId"));
		when(inventoryRepository.findById(agentRep.getId())).thenReturn(agentRep);
		DeviceCredentials deviceCredentials = new DeviceCredentials("testTenant", "testUsername", "testPasswoerd", "testAppkey", 
				agentRep.getId());
		when(deviceContextService.getCredentials()).thenReturn(deviceCredentials);
		when(deviceContextService.getContext()).thenReturn(new DeviceContext(deviceCredentials));
	}
	
}
