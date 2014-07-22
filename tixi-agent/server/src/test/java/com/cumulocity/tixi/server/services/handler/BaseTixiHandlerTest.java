package com.cumulocity.tixi.server.services.handler;

import static com.cumulocity.model.idtype.GId.asGId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;

import com.cumulocity.agent.server.context.DeviceContext;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.context.DeviceCredentials;
import com.cumulocity.agent.server.repository.IdentityRepository;
import com.cumulocity.agent.server.repository.MeasurementRepository;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.cumulocity.tixi.server.services.DeviceControlService;
import com.cumulocity.tixi.server.services.DeviceService;

public abstract class BaseTixiHandlerTest {
	
	protected DeviceContextService deviceContextService = mock(DeviceContextService.class);
	protected IdentityRepository identityRepository = mock(IdentityRepository.class);
	protected MeasurementRepository measurementRepository = mock(MeasurementRepository.class);
	protected LogDefinitionRegister logDefinitionRegister = mock(LogDefinitionRegister.class);
	protected DeviceControlService deviceControlService = mock(DeviceControlService.class);
	
	protected final FakeInventoryRepository inventoryRepository = new FakeInventoryRepository();
    protected final DeviceService deviceService = new DeviceService(identityRepository, mock(DeviceCredentialsApi.class), deviceContextService, inventoryRepository);
	protected ManagedObjectRepresentation agentRep;
	
	@Before
	public void init() throws Exception {
		agentRep = new ManagedObjectRepresentation();
		agentRep.setId(asGId("agentId"));
		when(inventoryRepositSSSory.findById(agentRep.getId())).thenReturn(agentRep);
		DeviceCredentials deviceCredentials = new DeviceCredentials("testTenant", "testUsername", "testPasswoerd", "testAppkey", 
				agentRep.getId());
		when(deviceContextService.getCredentials()).thenReturn(deviceCredentials);
		when(deviceContextService.getContext()).thenReturn(new DeviceContext(deviceCredentials));
	}
	
}
