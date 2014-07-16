package com.cumulocity.tixi.server.services.handler;

import static com.cumulocity.tixi.server.model.txml.LogDefinitionBuilder.aLogDefinition;
import static com.cumulocity.tixi.server.model.txml.LogDefinitionItemBuilder.anItem;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.tixi.server.model.SerialNumber;
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
		// @formatter:off
		LogDefinition logDefinition = aLogDefinition()
			.withNewItemSet("itemSet_1")
			.withItem(anItem()
				.withId("item_1")
				.withPath("/Process/agent1/device11/measure"))
			.withItem(anItem()
				.withId("item_2")
				.withPath("/Process/agent1/device12/measure"))
			.withItem(anItem()
				.withId("item_3")
				.withPath("/Process/agent2/device21/measure"))
			.withItem(anItem()
				.withId("item_4")
				.withPath("/Process/agent2/device21/measure"))
			.build();
		// @formatter:on
		
		handler.handle(logDefinition);
		
		assertThat(inventoryRepository.getAllExternalIds()).containsOnly(
				// @formatter:off
				new SerialNumber("agent1"),
				new SerialNumber("device11"),
				new SerialNumber("device12"),
				new SerialNumber("agent2"),
				new SerialNumber("device21"));
				// @formatter:on
    }
}
