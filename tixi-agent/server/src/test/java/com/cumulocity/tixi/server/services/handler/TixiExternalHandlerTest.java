package com.cumulocity.tixi.server.services.handler;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.txml.External;
import com.cumulocity.tixi.server.model.txml.ExternalBuilder;

public class TixiExternalHandlerTest extends BaseTixiHandlerTest {
	
	private TixiExternalHandler handler;
	private FakeInventoryRepository inventoryRepository = new FakeInventoryRepository();

	@Before
	public void init() {
		super.init();
		handler = new TixiExternalHandler(deviceContextService, inventoryRepository, measurementApi, logDefinitionRegister);
	}
	
	@Test
    public void shouldCreateCorrectDevicesAndAgents() throws Exception {		
		// @formatter:off
		External external = ExternalBuilder.anExternal()
				.withBus("Bus_1")
					.withDevice("Device_11")
						.withMeter("Meter_111")
						.withMeter("Meter_112")
					.withDevice("Device_12")
						.withMeter("Meter_123")
						.withMeter("Meter_124")
				.withBus("Bus_2")
					.withDevice("Device_21")
						.withMeter("Meter_211")
						.withMeter("Meter_212")
					.build();
		// @formatter:on
		
		handler.handle(external, "not important");
		
		assertThat(inventoryRepository.getAllExternalIds()).containsOnly(
				// @formatter:off
				new SerialNumber("Bus_1"),
				new SerialNumber("Device_11"),
				new SerialNumber("Device_12"),
				new SerialNumber("Bus_2"),
				new SerialNumber("Device_21"));
				// @formatter:on
    }
}
