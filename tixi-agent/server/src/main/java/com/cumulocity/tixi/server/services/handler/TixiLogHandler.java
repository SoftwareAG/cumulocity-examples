package com.cumulocity.tixi.server.services.handler;

import static com.cumulocity.tixi.server.model.ManagedObjects.asManagedObject;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.repository.MeasurementRepository;
import com.cumulocity.model.DateConverter;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.tixi.server.model.txml.*;
import com.cumulocity.tixi.server.services.DeviceControlService;
import com.cumulocity.tixi.server.services.DeviceService;
import com.google.common.base.Optional;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TixiLogHandler extends TixiHandler {
	
	private static final String AGENT_PROP_LAST_LOG_FILE_DATE = "lastLogFile";
	private static final Logger logger = LoggerFactory.getLogger(TixiLogHandler.class);
	private final DeviceControlService deviceControlService;
    private final MeasurementRepository measurementRepository;
    
	@Autowired
	public TixiLogHandler(DeviceContextService deviceContextService, DeviceService deviceService,
	        MeasurementRepository measurementRepository, LogDefinitionRegister logDefinitionRegister, DeviceControlService deviceControlService) {
	    super(deviceContextService, deviceService,  logDefinitionRegister);
        this.measurementRepository = measurementRepository;
		this.deviceControlService = deviceControlService;
    }
	
	public void handle(Log log, String recordName) {
		logger.info("Proccess log with id {} for record {}.", log.getId(), recordName);
		Measurements measurements = null;
		try {
			measurements = createMeasurements(log);
			saveMeasurements(measurements.getMeasurements());
		} catch (Exception ex) {
			logger.info("Log with id processing failed.", ex);
			deviceControlService.markAllOperationsFailed(tixiAgentId);
			return;
		}
		deviceControlService.markAllOperationsSuccess(tixiAgentId);		
        if(measurements.getLastProcessedDate() != null) {
			saveLastLogFileDateInAgent(measurements.getLastProcessedDate());
		}
        logger.info("Log proccessed.");
	}
	
	Measurements createMeasurements(Log log) {
		Measurements measurements = new Measurements(createProcessedDates());
		RecordDefinition recordDefinition  = getRecordDefinition();
		if(recordDefinition == null) {
			return measurements;
		}			
		for (RecordItemSet recordItemSet : log.getRecordItemSets()) {
			Date recordDateTime = recordItemSet.getDateTime();
			if(measurements.isNew(recordDateTime)) {
				measurements.addProcessedDate(recordItemSet.getDateTime());
				handleRecordItemSet(measurements, recordItemSet, recordDefinition);
			} else {
				logger.debug("Skip record with date {} as it have been already processed.", recordItemSet.getDateTime());
			}
		}
		return measurements;
	}

	private void handleRecordItemSet(Measurements measurements, RecordItemSet record, RecordDefinition recordDefinition) {
		logger.debug("Proccess log item set with id {} and date {}.", record.getId(), record.getDateTime());
	    for (RecordItem item : record.getRecordItems()) {
	    	RecordItemDefinition itemDef = recordDefinition.getRecordItemDefinition(item.getId());
	    	if(itemDef == null) {
	    		logger.warn("There is no log definition item for " +
	    				" itemId: {}; skip this log item.", item.getId());
	    		continue;
	    	}
	    	if(itemDef.getPath() == null) {
	    		logger.debug("Log definition item has no path variable " +
	    				"itemId: {}; skip this log item.", item.getId());
	    		continue;
	    	}
	    	
	    	handleRecordItem(measurements, item, itemDef, record.getDateTime());
	    }
	    logger.debug("Proccess log item set with id {} and date {}.", record.getId(), record.getDateTime());
    }
	
	private void handleRecordItem(Measurements measurements, RecordItem item, RecordItemDefinition itemDef, Date date) {
		logger.trace("Proccess log {} item with id.", item.getId());
		String deviceId = getDeviceIdOrDefault(itemDef.getPath());
		MeasurementRepresentation measurement = measurements.getOrCreateMeasurement(new MeasurementKey(deviceId, date));
		measurement.setProperty(asFragmentName(itemDef), asFragment(item));
		logger.trace("Item with id {} processed.", item.getId());
	}
	
	private String getDeviceIdOrDefault(RecordItemPath recordItemPath) {
	    if (recordItemPath instanceof DeviceVariablePath) {
	        return ((DeviceVariablePath) recordItemPath).getDeviceId();
	    }
	    return null;
	}

	private void saveMeasurements(Map<MeasurementKey, MeasurementRepresentation> measurements) {
	    for (Entry<MeasurementKey, MeasurementRepresentation> entry : measurements.entrySet()) {
	        MeasurementRepresentation measurement = entry.getValue();
	        String deviceId = entry.getKey().getDeviceId();
			ManagedObjectRepresentation source = Optional.fromNullable(deviceService.findDevice(deviceId)).or(asManagedObject(tixiAgentId));
	        if (source == null) {
	            continue;
	        }
			measurement.setSource(source);
			logger.debug("Create measurement {}.", measurement);
			measurementRepository.save(measurement);
        }
    }
	
	private RecordDefinition getRecordDefinition() {
		LogDefinition logDefinition = logDefinitionRegister.getLogDefinition();
		if (logDefinition == null) {
			return null;
		}
		if(logDefinition.getRecordIds().isEmpty()) {
			logger.info("Log definition has no records {}.", logDefinition);
			return null;
		}
		String recordId = logDefinition.getRecordIds().get(0).getId();
		RecordDefinition recordDefinition = logDefinition.getRecordDefinition(recordId);
		logger.info("Log definition has recordDefinition {} for recordId {}.", recordDefinition, recordId);
		return recordDefinition;
	}

	private Date createProcessedDates() {
		ManagedObjectRepresentation agentRep = deviceService.find(tixiAgentId);
		return getLastLogFileDate(agentRep);
	}
	
	private void saveLastLogFileDateInAgent(Date lastProcessedDate) {
	    ManagedObjectRepresentation agentRep = new ManagedObjectRepresentation();
		agentRep.setId(tixiAgentId);
		setLastLogFileDate(agentRep, lastProcessedDate);
		deviceService.update(agentRep);
    }

	private static Map<String, BigDecimal> asFragment(RecordItem logItem) {
		Map<String, BigDecimal> measurementValue = new HashMap<>();
		measurementValue.put("value", logItem.getValue());
	    return measurementValue;
    }
	
	private static String asFragmentName(RecordItemDefinition itemDef) {
	    return "c8y_" + itemDef.getPath().getName();
    }
	
	static void setLastLogFileDate(ManagedObjectRepresentation rep, Date date) {
		String dateStr = null;
		if(date != null) {
			dateStr = DateConverter.date2String(date);
		}
		rep.setProperty(AGENT_PROP_LAST_LOG_FILE_DATE, dateStr);
	}
	
	static class Measurements {
		private Date lastProcessedDate;
		private Set<Date> processedDates = new TreeSet<>();
		private final Map<MeasurementKey, MeasurementRepresentation> measurements = new HashMap<>();
		
		Measurements(Date lastProcessedDate) {
	        this.lastProcessedDate = lastProcessedDate;
        }

		Set<Date> getProcessedDates() {
			return processedDates;
		}
		
		void addProcessedDate(Date date) {
			lastProcessedDate = date;
			processedDates.add(date);
		}
		
		Date getLastProcessedDate() {
			return lastProcessedDate;
		}

		boolean isNew(Date date) {
			return lastProcessedDate == null || lastProcessedDate.before(date);
		}

		Map<MeasurementKey, MeasurementRepresentation> getMeasurements() {
			return measurements;
		}
		
		MeasurementRepresentation getMeasurement(MeasurementKey key) {
			return measurements.get(key);
		}
		
		private MeasurementRepresentation getOrCreateMeasurement(MeasurementKey key) {
			MeasurementRepresentation result = getMeasurement(key);
			if(result == null) {
				result = new MeasurementRepresentation();
				result.setType("c8y_tixiMeasurement");
				result.setTime(key.getDate());
				measurements.put(key, result);
			}
			return result;
		}
	}
	
	
	static class MeasurementKey {
		private String deviceId;
		private Date date;
		
		public MeasurementKey(String deviceId, Date date) {
			this.deviceId = deviceId;
			this.date = date;
		}
		
		public String getDeviceId() {
			return deviceId;
		}
		
		public Date getDate() {
			return date;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((date == null) ? 0 : date.hashCode());
			result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MeasurementKey other = (MeasurementKey) obj;
			if (date == null) {
				if (other.date != null)
					return false;
			} else if (!date.equals(other.date))
				return false;
			if (deviceId == null) {
				if (other.deviceId != null)
					return false;
			} else if (!deviceId.equals(other.deviceId))
				return false;
			return true;
		}
	}
	
	static Date getLastLogFileDate(ManagedObjectRepresentation rep) {
		Object dateStr = rep.getProperty(AGENT_PROP_LAST_LOG_FILE_DATE);
		if(dateStr == null) {
			return null;
		} else {
			return DateConverter.string2Date(String.valueOf(dateStr));
		}
	}
}