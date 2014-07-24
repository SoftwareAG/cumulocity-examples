package com.cumulocity.tixi.server.services.handler;

import static com.cumulocity.tixi.server.model.txml.LogBuilder.aLog;
import static com.cumulocity.tixi.server.model.txml.LogDefinitionBuilder.aLogDefinition;
import static com.cumulocity.tixi.server.model.txml.RecordItemDefinitionBuilder.anItem;
import static com.cumulocity.tixi.server.services.handler.TixiLogHandler.getLastLogFileDate;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.cumulocity.tixi.server.services.handler.TixiLogHandler.MeasurementKey;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.txml.Log;
import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.cumulocity.tixi.server.services.handler.TixiLogHandler.ProcessedDates;

public class TixiLogHandlerTest extends BaseTixiHandlerTest {

	private TixiLogHandler tixiLogHandler;

    private ArgumentCaptor<MeasurementRepresentation> measurementCaptor;


    @Before
    public void init() throws Exception {
        super.init();
        tixiLogHandler = new TixiLogHandler(deviceContextService, deviceService, measurementRepository, logDefinitionRegister, deviceControlService);
        tixiLogHandler.afterPropertiesSet();
        measurementCaptor = ArgumentCaptor.forClass(MeasurementRepresentation.class);
    }

    @Test
	public void shouldSendCorrectMeasurementsAndUpdateLogFileDate() throws Exception {
		// @formatter:off
		LogDefinition logDefinition = aLogDefinition()
			.withNewRecordDef()
				.withRecordItemDef(anItem()
					.withId("item_1")
					.withPath("/Process/agent1/device1/measure1"))
				.withRecordItemDef(anItem()
					.withId("item_2")
					.withPath("/Process/agent1/device1/measure2"))
			.build();
		
		Log log = aLog()
			.withNewRecord(asDate(15))
				.withRecordItem("item_1", BigDecimal.valueOf(1))
				.withRecordItem("item_2", BigDecimal.valueOf(2))
			.withNewRecord(asDate(20))
			.build();
		// @formatter:on
		when(logDefinitionRegister.getLogDefinition()).thenReturn(logDefinition);
		inventoryRepository.save( new ManagedObjectRepresentation() , new SerialNumber("device1"));
		
		tixiLogHandler.handle(log, "itemSet_1");
		
		verify(measurementRepository, times(1)).save(measurementCaptor.capture());
		MeasurementRepresentation rep = measurementCaptor.getValue();
		assertThat(rep.get("c8y_measure1")).isEqualTo(aMeasurementValue(1));
		assertThat(rep.get("c8y_measure2")).isEqualTo(aMeasurementValue(2));
		assertThat(rep.getType()).isEqualTo("c8y_tixiMeasurement");
		
		assertThat(getLastLogFileDate(inventoryRepository.findById(agentRep.getId()))).isEqualTo(asDate(20));
	}
	
	@Test
    public void shouldSendCorrectMeasurementsForProcessVariables() throws Exception {
        // @formatter:off
        LogDefinition logDefinition = aLogDefinition()
            .withNewRecordDef()
	            .withRecordItemDef(anItem()
	                .withId("EnergieDiff")
	                .withPath("/Process/PV/EnergieDiff"))
	            .withRecordItemDef(anItem()
	                .withId("PiValue")
	                .withPath("/Process/PV/PiValue"))
            .build();
        
        Log log = aLog()
            .withNewRecord(asDate(15))
                .withRecordItem("EnergieDiff", BigDecimal.valueOf(1))
                .withRecordItem("PiValue", BigDecimal.valueOf(2))
            .withNewRecord(asDate(20))
            .build();
        // @formatter:on
        when(logDefinitionRegister.getLogDefinition()).thenReturn(logDefinition);
        
        tixiLogHandler.handle(log, "itemSet_1");
        
        verify(measurementRepository, Mockito.times(1)).save(measurementCaptor.capture());
        MeasurementRepresentation rep = measurementCaptor.getValue();
        assertThat(rep.get("c8y_EnergieDiff")).isEqualTo(aMeasurementValue(1));
        assertThat(rep.get("c8y_PiValue")).isEqualTo(aMeasurementValue(2));
    }
	
	@Test
	public void shouldProcessItemSetsWithLaterDateOnly() throws Exception {
		// @formatter:off
        Log log = aLog()
				.withNewRecord(asDate(15))
				.withNewRecord(asDate(20))
				.build();
		// @formatter:on
		when(logDefinitionRegister.getLogDefinition()).thenReturn(
				aLogDefinition().withNewRecordDef().build());
		TixiLogHandler.setLastLogFileDate(agentRep, asDate(18));
		
		ProcessedDates processedDates = tixiLogHandler.createMeasurements(log);
		
		assertThat(processedDates.getProcessed()).containsOnly(asDate(20));
	}
	
	@Test
	public void shouldProcessItemSetsForFirstRecordOnly() throws Exception {
		// @formatter:off
		LogDefinition logDefinition = aLogDefinition()
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
            .withNewRecord(asDate(20))
                .withRecordItem("EnergieDiff", BigDecimal.valueOf(1))
            .withNewRecord(asDate(25))
                .withRecordItem("PiValue", BigDecimal.valueOf(2))
            .build();
		// @formatter:on
		when(logDefinitionRegister.getLogDefinition()).thenReturn(logDefinition);
		
		tixiLogHandler.createMeasurements(log);
		
		assertThat(tixiLogHandler.measurements.keySet()).containsOnly(new MeasurementKey(null, asDate(20)));
	}
	
	private static Map<String, Object> aMeasurementValue(int value) {
		Map<String, Object> measurementValue = new HashMap<>();
		measurementValue.put("value", BigDecimal.valueOf(value));
		return measurementValue;
	}
	
	private static Date asDate(int time) {
        return new Date(time);
	}
}
