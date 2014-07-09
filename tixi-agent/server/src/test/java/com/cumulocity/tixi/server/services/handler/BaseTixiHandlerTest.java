package com.cumulocity.tixi.server.services.handler;

import static org.mockito.Mockito.mock;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.repository.IdentityRepository;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.sdk.client.measurement.MeasurementApi;

public abstract class BaseTixiHandlerTest {
	
	protected DeviceContextService deviceContextService = mock(DeviceContextService.class);
	protected IdentityRepository identityRepository = mock(IdentityRepository.class);
	protected InventoryRepository inventoryRepository = mock(InventoryRepository.class);
	protected MeasurementApi measurementApi = mock(MeasurementApi.class);
	protected LogDefinitionRegister logDefinitionRegister = mock(LogDefinitionRegister.class);
	

}
