package com.cumulocity.tixi.server.services.handler;

import static com.cumulocity.tixi.server.model.txml.LogDefinitionBuilder.aLogDefinition;
import static com.cumulocity.tixi.server.model.txml.LogDefinitionItemBuilder.anItem;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.txml.LogDefinition;

public class TixiLogDefinitionHandlerTest extends BaseTixiHandlerTest {
	
	private TixiLogDefinitionHandler handler;
	
	private ArgumentCaptor<SerialNumber> snCaptor; 
	private FakeInventoryRepository inventoryRepository = new FakeInventoryRepository();

	@Before
	public void init() {
		handler = new TixiLogDefinitionHandler(deviceContextService, identityRepository, inventoryRepository, measurementApi, logDefinitionRegister);
		snCaptor = ArgumentCaptor.forClass(SerialNumber.class);
	}
	
	@Test
    public void shouldCreateCorrectDevicesAndAgents() throws Exception {
		// @formatter:off
		LogDefinition logDefinition = aLogDefinition()
			.withNewItemSet("itemSet_1")
			.withItem(anItem()
				.withId("item_1")
				.withPath("/Process/agent1/device1/measure"))
			.withItem(anItem()
				.withId("item_2")
				.withPath("/Process/agent1/device2/measure"))
			.build();
		// @formatter:on
		
		handler.handle(logDefinition);
		
		verify(identityRepository, times(4)).save(any(GId.class), snCaptor.capture());
		assertThat(snCaptor.getAllValues()).containsExactly(
				new SerialNumber("agent1"),
				new SerialNumber("device1"),
				new SerialNumber("agent1"),
				new SerialNumber("device2"));
    }
}
