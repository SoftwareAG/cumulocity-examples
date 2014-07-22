package com.cumulocity.tixi.server.services.handler;

import static com.cumulocity.tixi.server.model.txml.LogBuilder.aLog;
import static com.cumulocity.tixi.server.model.txml.LogDefinitionBuilder.aLogDefinition;
import static com.cumulocity.tixi.server.model.txml.LogDefinitionItemBuilder.anItem;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

public class TixiLogHandlerTest extends BaseTixiHandlerTest {

	private TixiLogHandler tixiLogHandler;
	private ArgumentCaptor<MeasurementRepresentation> measurementCaptor;
	private ArgumentCaptor<ManagedObjectRepresentation> moCaptor;
	private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	@Before
	public void init() throws Exception {
		super.init();
		tixiLogHandler = new TixiLogHandler(deviceContextService, inventoryRepository, 
				measurementApi, logDefinitionRegister, deviceControlRepository);
		tixiLogHandler.afterPropertiesSet();
		measurementCaptor = ArgumentCaptor.forClass(MeasurementRepresentation.class);
		moCaptor = ArgumentCaptor.forClass(ManagedObjectRepresentation.class);
	}

	@Test
	public void shouldSendCorrectMeasurementsAndUpdateLogFileDate() throws Exception {
		// @formatter:off
		LogDefinition logDefinition = aLogDefinition()
			.withNewRecordDefinition("itemSet_1")
			.withRecordItemDefinition(anItem()
				.withId("item_1")
				.withPath("/Process/agent1/device1/measure1"))
			.withRecordItemDefinition(anItem()
				.withId("item_2")
				.withPath("/Process/agent1/device1/measure2"))
			.build();
		
		Log log = aLog()
			.withId("itemSet_1")
			.withNewItemSet("sth", asDate("2000-10-15"))
				.withItem("item_1", BigDecimal.valueOf(1))
				.withItem("item_2", BigDecimal.valueOf(2))
			.withNewItemSet("sth", asDate("2000-10-20"))
			.build();
		// @formatter:on
		when(logDefinitionRegister.getLogDefinition()).thenReturn(logDefinition);
		inventoryRepository.save( new ManagedObjectRepresentation() , new SerialNumber("device1"));
		
		tixiLogHandler.handle(log, "itemSet_1");
		
		verify(measurementRepository, Mockito.times(1)).save(measurementCaptor.capture());
		MeasurementRepresentation rep = measurementCaptor.getValue();
		assertThat(rep.get("c8y_measure1")).isEqualTo(aMeasurementValue(1));
		assertThat(rep.get("c8y_measure2")).isEqualTo(aMeasurementValue(2));
		assertThat(rep.getType()).isEqualTo("c8y_tixiMeasurement");
		
		assertThat(moCaptor.getValue().getProperty(AGENT_PROP_LAST_LOG_FILE_DATE)).isEqualTo(asDate("2000-10-20"));
	}
	
	@Test
    public void shouldSendCorrectMeasurementsForProcessVariables() throws Exception {
        // @formatter:off
        LogDefinition logDefinition = aLogDefinition()
            .withNewRecordDefinition("itemSet_1")
            .withRecordItemDefinition(anItem()
                .withId("EnergieDiff")
                .withPath("/Process/PV/EnergieDiff"))
            .withRecordItemDefinition(anItem()
                .withId("PiValue")
                .withPath("/Process/PV/PiValue"))
            .build();
        
        Log log = aLog()
            .withId("itemSet_1")
            .withNewItemSet("sth", asDate("2000-10-15"))
                .withItem("EnergieDiff", BigDecimal.valueOf(1))
                .withItem("PiValue", BigDecimal.valueOf(2))
            .withNewItemSet("sth", asDate("2000-10-20"))
            .build();
        // @formatter:on
        when(logDefinitionRegister.getLogDefinition()).thenReturn(logDefinition);
        
        tixiLogHandler.handle(log, "itemSet_1");
        
        verify(measurementApi, Mockito.times(1)).create(measurementCaptor.capture());
        MeasurementRepresentation rep = measurementCaptor.getValue();
        assertThat(rep.get("c8y_EnergieDiff")).isEqualTo(aMeasurementValue(1));
        assertThat(rep.get("c8y_PiValue")).isEqualTo(aMeasurementValue(2));
    }
	
	@Test
	public void shouldProcessItemSetsWithLaterDateOnly() throws Exception {
		// @formatter:off
		Log log = aLog()
				.withId("dataloggin_1")
				.withNewItemSet("sth1", asDate("2000-10-15"))
				.withNewItemSet("sth2", asDate("2000-10-20"))
				.build();
		// @formatter:on
		when(logDefinitionRegister.getLogDefinition()).thenReturn(aLogDefinition().build());
		agentRep.setProperty(AGENT_PROP_LAST_LOG_FILE_DATE, asDate("2000-10-18"));
		
		tixiLogHandler.handle(log, "dataloggin_1");
		
		assertThat(tixiLogHandler.processedDates.getProcessed()).containsOnly(asDate("2000-10-20"));
		
	}
	
	private static Map<String, Object> aMeasurementValue(int value) {
		Map<String, Object> measurementValue = new HashMap<>();
		measurementValue.put("value", BigDecimal.valueOf(value));
		return measurementValue;
	}
	
	private static Date asDate(String date) {
		try {
	        return DATE_FORMAT.parse(date);
        } catch (ParseException e) {
        	throw new RuntimeException(e);
        }
	}
}
