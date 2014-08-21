package com.cumulocity.tixi.server.services.handler;

import static com.cumulocity.tixi.server.model.txml.LogBuilder.aLog;
import static com.cumulocity.tixi.server.model.txml.LogDefinitionBuilder.aLogDefinition;
import static com.cumulocity.tixi.server.model.txml.RecordItemDefinitionBuilder.anItem;
import static com.cumulocity.tixi.server.services.handler.TixiLogHandler.getLastLogFileDate;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.txml.Log;
import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.cumulocity.tixi.server.model.txml.LogDefinitionBuilder;
import com.cumulocity.tixi.server.services.handler.TixiLogHandler.MeasurementKey;
import com.cumulocity.tixi.server.services.handler.TixiLogHandler.Measurements;

public class TixiLogHandlerTest extends BaseTixiHandlerTest {

	private TixiLogHandler tixiLogHandler;

    @Before
    public void init() throws Exception {
        super.init();
        tixiLogHandler = new TixiLogHandler(deviceContextService, deviceService, measurementRepository, logDefinitionRegister, deviceControlService);
        tixiLogHandler.afterPropertiesSet();
    }

    @Test
	public void shouldSendCorrectMeasurementsAndUpdateLogFileDate() throws Exception {
		// @formatter:off
    	registeredLogDefinition()
			.withNewRecordDef()
				.withRecordItemDef(anItem()
					.withId("item_1")
					.withPath("/Process/agent1/device1/measure1"))
				.withRecordItemDef(anItem()
					.withId("item_2")
					.withPath("/Process/agent1/device1/measure2"))
			.build();
		
		Log log = aLog()
			.withNewRecordItemSet(asDate(15))
				.withRecordItem("item_1", BigDecimal.valueOf(1))
				.withRecordItem("item_2", BigDecimal.valueOf(2))
			.withNewRecordItemSet(asDate(20))
			.build();
		// @formatter:on
		inventoryRepository.save(new ManagedObjectRepresentation() , new SerialNumber("device1"));
		ArgumentCaptor<MeasurementRepresentation> measurementCaptor = ArgumentCaptor.forClass(MeasurementRepresentation.class);
		tixiLogHandler.handle(log, "record_1");
		
		verify(measurementRepository, times(1)).save(measurementCaptor.capture());
		MeasurementRepresentation actual = measurementCaptor.getValue();
		assertThat(actual.get("c8y_measure1")).isEqualTo(aMeasurementValue("measure1", 1));
		assertThat(actual.get("c8y_measure2")).isEqualTo(aMeasurementValue("measure2", 2));
		assertThat(actual.getType()).isEqualTo("c8y_tixiMeasurement");
		
		assertThat(getLastLogFileDate(inventoryRepository.findById(agentRep.getId()))).isEqualTo(asDate(20));
	}
	
	@Test
    public void shouldSendCorrectMeasurementsForProcessVariables() throws Exception {
        // @formatter:off
		registeredLogDefinition()
            .withNewRecordDef()
	            .withRecordItemDef(anItem()
	                .withId("EnergieDiff")
	                .withPath("/Process/PV/EnergieDiff"))
	            .withRecordItemDef(anItem()
	                .withId("PiValue")
	                .withPath("/Process/PV/PiValue"))
            .build();
        
        Log log = aLog()
            .withNewRecordItemSet(asDate(15))
                .withRecordItem("EnergieDiff", BigDecimal.valueOf(1))
                .withRecordItem("PiValue", BigDecimal.valueOf(2))
            .withNewRecordItemSet(asDate(20))
            .build();
        // @formatter:on
        
        Measurements measurements = tixiLogHandler.createMeasurements(log);
        
        MeasurementKey measurementKey = new MeasurementKey(null, asDate(15));
		assertThat(measurements.getMeasurements().keySet()).containsOnly(measurementKey);
		MeasurementRepresentation rep = measurements.getMeasurement(measurementKey);
        assertThat(rep.get("c8y_EnergieDiff")).isEqualTo(aMeasurementValue("EnergieDiff", 1));
        assertThat(rep.get("c8y_PiValue")).isEqualTo(aMeasurementValue("PiValue", 2));
    }
	
	@Test
	public void shouldProcessItemSetsWithLaterDateOnly() throws Exception {
		// @formatter:off
        Log log = aLog()
				.withNewRecordItemSet(asDate(15))
				.withNewRecordItemSet(asDate(20))
				.build();
		// @formatter:on
		when(logDefinitionRegister.getLogDefinition()).thenReturn(
				aLogDefinition().withNewRecordDef().build());
		TixiLogHandler.setLastLogFileDate(agentRep, asDate(18));
		
		Measurements measurements = tixiLogHandler.createMeasurements(log);
		
		assertThat(measurements.getProcessedDates()).containsOnly(asDate(20));
	}
	
	@Test
	public void shouldProcessItemSetsForFirstRecordOnly() throws Exception {
		// @formatter:off
		registeredLogDefinition()
			.withNewRecordDef()
				.withRecordItemDef(anItem()
					.withId("EnergieDiff")
					.withPath("/Process/PV/EnergieDiff"))
			.withNewRecordDef()
				.withRecordItemDef(anItem()
					.withId("PiValue")
					.withPath("/Process/PV/PiValue"))
			.build();
		
        Log log = aLog()
            .withNewRecordItemSet(asDate(20))
                .withRecordItem("EnergieDiff", BigDecimal.valueOf(1))
            .withNewRecordItemSet(asDate(25))
                .withRecordItem("PiValue", BigDecimal.valueOf(2))
            .build();
		// @formatter:on
		
		Measurements measurements = tixiLogHandler.createMeasurements(log);
		
		assertThat(measurements.getMeasurements().keySet()).containsOnly(new MeasurementKey(null, asDate(20)));
	}
	
	private static Map<String, Map<String, BigDecimal>> aMeasurementValue(String name, int value) {
	    Map<String, Map<String, BigDecimal>> measurement = new HashMap<>();
		Map<String, BigDecimal> measurementValue = new HashMap<>();
		measurementValue.put("value", BigDecimal.valueOf(value));
		measurement.put(name, measurementValue);
		return measurement;
	}
	
	private static Date asDate(int time) {
        return new Date(time);
	}
	
	private LogDefinitionBuilder registeredLogDefinition() {
		return new LogDefinitionBuilder() {

			@Override
            public LogDefinition build() {
	            LogDefinition result = super.build();
	            when(logDefinitionRegister.getLogDefinition()).thenReturn(result);
	            return result;
            }
			
		};
	}
}
