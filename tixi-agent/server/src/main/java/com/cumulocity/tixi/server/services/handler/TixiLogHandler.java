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
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.txml.*;
import com.cumulocity.tixi.server.services.DeviceControlService;
import com.cumulocity.tixi.server.services.DeviceService;
import com.google.common.base.Optional;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TixiLogHandler extends TixiHandler {
	
	static final String AGENT_PROP_LAST_LOG_FILE_DATE = "lastLogFile";
	private static final Logger logger = LoggerFactory.getLogger(TixiLogHandler.class);
	private DeviceControlService deviceControlService;
    private MeasurementRepository measurementRepository;
    
    private Map<MeasurementKey, MeasurementRepresentation> measurements = new HashMap<>();
    ProcessedDates processedDates;
    private RecordDefinition recordDefinition;

	@Autowired
	public TixiLogHandler(DeviceContextService deviceContextService, DeviceService deviceService,
	        MeasurementRepository measurementRepository, LogDefinitionRegister logDefinitionRegister, DeviceControlService deviceControlService) {
	    super(deviceContextService, deviceService,  logDefinitionRegister);
        this.measurementRepository = measurementRepository;
		this.deviceControlService = deviceControlService;
    }
	
	public void handle(Log log, String recordName) {
		logger.info("Proccess log with id {} for record {}.", log.getId(), recordName);
		createProcessedDates();
		try {
			createMeasurements(log);
			saveMeasurements();
		} catch (Exception ex) {
			logger.info("Log with id processing failed.", ex);
			deviceControlService.markAllOperationsFailed(tixiAgentId);
			return;
		}
		deviceControlService.markAllOperationsSuccess(tixiAgentId);		
        updateProcessedDates();
        logger.info("Log proccessed.");
	}
	
	private void createMeasurements(Log log) {
		LogDefinition logDefinition = logDefinitionRegister.getLogDefinition();
		if (logDefinition == null) {
			return;
		}
		if(logDefinition.getRecordIds().isEmpty()) {
			logger.info("Log definition has no records {}.", logDefinition);
			return;
		}
		String recordId = logDefinition.getRecordIds().get(0).getId();
		recordDefinition = logDefinition.getRecordDefinition(recordId);
		if(recordDefinition == null) {
			logger.info("Log definition has no recordDefinition for recordId {}.", recordId);
			return;
		}			
		for (Record itemSet : log.getRecords()) {
			handleItemSet(itemSet);
		}
	}

    private void updateProcessedDates() {
        if(processedDates.getLast() != null) {
			saveLastLogFileDateInAgent(processedDates.getLast());
		}
    }

	private void createProcessedDates() {
		ManagedObjectRepresentation agentRep = deviceService.find(tixiAgentId);
		Date lastLogFile = (Date) agentRep.getProperty(AGENT_PROP_LAST_LOG_FILE_DATE);
		processedDates = new ProcessedDates(lastLogFile);
	}

	private void saveLastLogFileDateInAgent(Date lastProcessedDate) {
	    ManagedObjectRepresentation agentRep = new ManagedObjectRepresentation();
		agentRep.setId(tixiAgentId);
		agentRep.setProperty(AGENT_PROP_LAST_LOG_FILE_DATE, lastProcessedDate);
		deviceService.update(agentRep);
    }

	private void handleItemSet(Record itemSet) {
		logger.debug("Proccess log item set with id {} and date {}.", itemSet.getId(), itemSet.getDateTime());
		if(!processedDates.isNew(itemSet.getDateTime())) {
			return;
		}
		processedDates.add(itemSet.getDateTime());
	    for (RecordItem item : itemSet.getRecordItems()) {
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
	    	
	    	handleLogItem(item, itemDef, itemSet.getDateTime());
	    }
	    logger.debug("Proccess log item set with id {} and date {}.", itemSet.getId(), itemSet.getDateTime());
    }
	
	private void handleLogItem(RecordItem item, RecordItemDefinition itemDef, Date date) {
		logger.trace("Proccess log {} item with id.", item.getId());
		String deviceId = getDeviceIdOrDefault(itemDef.getPath());
		MeasurementRepresentation measurement = getMeasurement(new MeasurementKey(deviceId, date));
		measurement.setProperty(asFragmentName(itemDef), asFragment(item));
		logger.trace("Item with id {} processed.", item.getId());
	}
	
	private String getDeviceIdOrDefault(RecordItemPath recordItemPath) {
	    if (recordItemPath instanceof DeviceVariablePath) {
	        return ((DeviceVariablePath) recordItemPath).getDeviceId();
	    }
	    return null;
	}

	private void saveMeasurements() {
	    for (Entry<MeasurementKey, MeasurementRepresentation> entry : measurements.entrySet()) {
	        MeasurementRepresentation measurement = entry.getValue();
	        String deviceId = entry.getKey().getDeviceId();
			ManagedObjectRepresentation source = Optional.fromNullable(deviceService.find(new SerialNumber(deviceId))).or(asManagedObject(tixiAgentId));
	        if (source == null) {
	            continue;
	        }
			measurement.setSource(source);
			logger.debug("Create measurement {}.", measurement);
			measurementRepository.save(measurement);
        }
    }

	private static Map<String, BigDecimal> asFragment(RecordItem logItem) {
		Map<String, BigDecimal> measurementValue = new HashMap<>();
		measurementValue.put("value", logItem.getValue());
	    return measurementValue;
    }
	
	private static String asFragmentName(RecordItemDefinition itemDef) {
	    return "c8y_" + itemDef.getPath().getName();
    }
		
	private MeasurementRepresentation getMeasurement(MeasurementKey key) {
		MeasurementRepresentation result = measurements.get(key);
		if(result == null) {
			result = new MeasurementRepresentation();
			result.setType("c8y_tixiMeasurement");
			result.setTime(key.getDate());
			measurements.put(key, result);
		}
		return result;
	}
	
	private static class MeasurementKey {
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
	
	static class ProcessedDates {

		private Date last;
		private Set<Date> processed = new TreeSet<>();
		
		ProcessedDates(Date last) {
	        this.last = last;
        }

		
		void add(Date date) {
			last = date;
			processed.add(date);
		}
		
		boolean isNew(Date date) {
			return last == null || last.before(date);
		}

		Set<Date> getProcessed() {
			return processed;
		}

		Date getLast() {
			return last;
		}
	}
}