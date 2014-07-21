package com.cumulocity.tixi.server.services.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.cumulocity.tixi.server.model.txml.LogDefinition;

public class TixiLogDefinitionHandlerTest extends BaseTixiHandlerTest {
	
	private TixiLogDefinitionHandler handler;
	private FakeInventoryRepository inventoryRepository = new FakeInventoryRepository();

	@Before
	public void init() {
		super.init();
		handler = new TixiLogDefinitionHandler(deviceContextService, inventoryRepository, measurementApi, logDefinitionRegister);
	}
	
	@Test
    public void shouldCreateCorrectDevicesAndAgents() throws Exception {
		LogDefinition logDefinition = new LogDefinition();
		
		handler.handle(logDefinition);

		Mockito.verify(logDefinitionRegister).register(logDefinition);
    }
}
