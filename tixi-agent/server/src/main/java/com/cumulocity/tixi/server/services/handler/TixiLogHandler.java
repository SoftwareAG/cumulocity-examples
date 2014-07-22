package com.cumulocity.tixi.server.services.handler;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.repository.DeviceControlRepository;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.txml.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TixiLogHandler extends TixiHandler {
	
	static final String AGENT_PROP_LAST_LOG_FILE_DATE = "lastLogFile";
	private static final Logger logger = LoggerFactory.getLogger(TixiLogHandler.class);
	private DeviceControlRepository deviceControlRepository;

	@Autowired
	public TixiLogHandler(DeviceContextService deviceContextService, InventoryRepository inventoryRepository,
            MeasurementApi measurementApi, LogDefinitionRegister logDefinitionRegister, DeviceControlRepository deviceControlRepository) {
	    super(deviceContextService, inventoryRepository, measurementApi, logDefinitionRegister);
		this.deviceControlRepository = deviceControlRepository;
    }
	
	private Map<MeasurementKey, MeasurementRepresentation> measurements = new HashMap<>();
	private LogDefinition logDefinition;
	private String logId;
	ProcessedDates processedDates;
	
	public void handle(Log log, String recordName) {
		processedDates = createProcessedDates();
		try {
			this.logId = log.getId();
			logger.info("Proccess log with id {} for record {}.", logId, recordName);
			this.logDefinition = logDefinitionRegister.getLogDefinition();
			if (logDefinition == null) {
				return;
			}
			for (LogItemSet itemSet : log.getItemSets()) {
				handleItemSet(itemSet, recordName);
			}
			saveMeasurements();
			logger.info("Log with id {} proccessed.", logId);
		} catch (Exception ex) {
			logger.info("Log with id " + logId + " processing failed.", ex);
			deviceControlRepository.markAllOperationsFailed(tixiAgentId);
			return;
		}
		deviceControlRepository.markAllOperationsSuccess(tixiAgentId);
		if(processedDates.getLast() != null) {
			saveLastLogFileDateInAgent(processedDates.getLast());
		}
	}

	private ProcessedDates createProcessedDates() {
		ManagedObjectRepresentation agentRep = inventoryRepository.findById(tixiAgentId);
		Date lastLogFile = (Date) agentRep.getProperty(AGENT_PROP_LAST_LOG_FILE_DATE);
		return new ProcessedDates(lastLogFile);
    }

	private void saveLastLogFileDateInAgent(Date lastProcessedDate) {
	    ManagedObjectRepresentation agentRep = new ManagedObjectRepresentation();
		agentRep.setId(tixiAgentId);
		agentRep.setProperty(AGENT_PROP_LAST_LOG_FILE_DATE, lastProcessedDate);
		inventoryRepository.save(agentRep);
    }

	private void handleItemSet(LogItemSet itemSet, String recordName) {
		logger.debug("Proccess log item set with id {} and date {}.", itemSet.getId(), itemSet.getDateTime());
		if(!processedDates.isNew(itemSet.getDateTime())) {
			return;
		}
		processedDates.add(itemSet.getDateTime());
	    for (LogItem item : itemSet.getItems()) {
	    	RecordItemDefinition itemDef = logDefinition.getItem(recordName, item.getId());
	    	if(itemDef == null) {
	    		logger.warn("There is no log definition item for record: {}, itemSetId: {}," +
	    				" itemId: {}; skip this log item.", recordName, logId, item.getId());
	    		continue;
	    	}
	    	if(itemDef.getPath() == null) {
	    		logger.debug("Log definition item has no path variable " +
	    				"itemSetId: {} itemId: {}; skip this log item.", logId, item.getId());
	    		continue;
	    	}
	    	
	    	handleLogItem(item, itemDef, itemSet.getDateTime());
	    }
	    logger.debug("Proccess log item set with id {} and date {}.", itemSet.getId(), itemSet.getDateTime());
    }
	
	private void handleLogItem(LogItem item, RecordItemDefinition itemDef, Date date) {
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
	        ManagedObjectRepresentation source = getSource(deviceId);
	        if (source == null) {
	            continue;
	        }
			measurement.setSource(source);
			logger.debug("Create measurement {}.", measurement);
			measurementApi.create(measurement);
        }
    }

    private ManagedObjectRepresentation getSource(String deviceId) {
        if (deviceId == null) {
            return defaultSource();
        }
        return getSourceBySerialNumber(deviceId);
    }

    private ManagedObjectRepresentation getSourceBySerialNumber(String deviceId) {
        SerialNumber deviceIdSerial = new SerialNumber(deviceId);
        try {
        	return inventoryRepository.findByExternalId(deviceIdSerial);
        } catch (SDKException ex) {
        	logger.warn("Cannot find source for {}.", deviceIdSerial);
        }
        return null;
    }

    private ManagedObjectRepresentation defaultSource() {
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(tixiAgentId);
        return source;
    }


	private static Map<String, BigDecimal> asFragment(LogItem logItem) {
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
