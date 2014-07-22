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
	public void init() throws Exception {
		super.init();
		handler = new TixiExternalHandler(deviceContextService, inventoryRepository, measurementApi, logDefinitionRegister);
		handler.afterPropertiesSet();
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
		
		handler.handle(external);
		
		assertThat(inventoryRepository.getAllExternalIds()).containsOnly(
				// @formatter:off
				deviceSerial("Bus_1"),
				deviceSerial("Device_11"),
				deviceSerial("Device_12"),
				deviceSerial("Bus_2"),
				deviceSerial("Device_21"));
				// @formatter:on
    }
	
	private SerialNumber deviceSerial(String deviceName) {
		return new SerialNumber(deviceName + "_" + agentRep.getId().getValue());
				
	}
}
