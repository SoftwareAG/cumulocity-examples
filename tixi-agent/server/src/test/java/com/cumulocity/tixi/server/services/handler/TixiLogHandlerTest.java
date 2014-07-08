package com.cumulocity.tixi.server.services.handler;

import static com.cumulocity.tixi.server.model.txml.LogBuilder.aLog;
import static com.cumulocity.tixi.server.model.txml.LogDefinitionBuilder.aLogDefinition;
import static com.cumulocity.tixi.server.model.txml.LogDefinitionItemBuilder.anItem;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.tixi.server.model.txml.Log;
import com.cumulocity.tixi.server.model.txml.LogDefinition;

public class TixiLogHandlerTest extends BaseTixiHandlerTest {

	private TixiLogHandler tixiLogHandler;
	private ArgumentCaptor<MeasurementRepresentation> measurementCaptor; 

	@Before
	public void init() {
		tixiLogHandler = new TixiLogHandler(deviceContextService, identityRepository, inventoryRepository, measurementApi, logDefinitionRegister);
		measurementCaptor = ArgumentCaptor.forClass(MeasurementRepresentation.class);
	}

	@Test
	public void shouldSendCorrectMeasurements() throws Exception {
		// @formatter:off
		LogDefinition logDefinition = aLogDefinition()
			.withNewItemSet("itemSet_1")
			.withItem(anItem()
				.withId("item_1")
				.withPath("/Process/agent1/device1/measure1"))
			.withItem(anItem()
				.withId("item_2")
				.withPath("/Process/agent1/device1/measure2"))
			.build();
		
		Log log = aLog()
			.withId("itemSet_1")
			.withNewItemSet("sth", new Date())
				.withItem("item_1", BigDecimal.valueOf(1))
				.withItem("item_2", BigDecimal.valueOf(2))
			.build();
		// @formatter:on
		when(logDefinitionRegister.getLogDefinition()).thenReturn(logDefinition);
		
		tixiLogHandler.handle(log);
		
		verify(measurementApi, Mockito.times(2)).create(measurementCaptor.capture());
		
		
	}

}
