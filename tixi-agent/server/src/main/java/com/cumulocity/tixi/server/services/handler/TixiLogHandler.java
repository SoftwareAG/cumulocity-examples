package com.cumulocity.tixi.server.services.handler;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import com.cumulocity.tixi.server.model.txml.Log;
import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.cumulocity.tixi.server.model.txml.LogDefinitionItem;
import com.cumulocity.tixi.server.model.txml.LogItem;
import com.cumulocity.tixi.server.model.txml.LogItemSet;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TixiLogHandler extends TixiHandler<Log> {
	
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
	
	@Override
	public void handle(Log log) {
		try {
			this.logId = log.getId();
			logger.info("Proccess log with id {}.", logId);
			this.logDefinition = logDefinitionRegister.getLogDefinition();
			if (logDefinition == null) {
				return;
			}
			for (LogItemSet itemSet : log.getItemSets()) {
				handleItemSet(itemSet);
			}
			saveMeasurements();
			logger.info("Log with id {} proccessed.", logId);
		} catch (Exception ex) {
			logger.info("Log with id {} processing failed.", ex);
			deviceControlRepository.markAllOperationsFailed(agentId);
			return;
		}
		deviceControlRepository.markAllOperationsSuccess(agentId);
	}

	private void handleItemSet(LogItemSet itemSet) {
		logger.debug("Proccess log item set with id {} and date {}.", itemSet.getId(), itemSet.getDateTime());
	    for (LogItem item : itemSet.getItems()) {
	    	LogDefinitionItem itemDef = logDefinition.getItem(logId, item.getId());
	    	if(itemDef == null) {
	    		logger.warn("There is no log definition item for itemSetId: {}," +
	    				" itemId: {}; skip this log item.", logId, item.getId());
	    		continue;
	    	}
	    	if(!isDevicePath(itemDef)) {
	    		logger.debug("Log definition item has no device path variable " +
	    				"itemSetId: {} itemId: {}; skip this log item.", logId, item.getId());
	    		continue;
	    	}
	    	
	    	handleLogItem(item, itemDef, itemSet.getDateTime());
	    }
	    logger.debug("Proccess log item set with id {} and date {}.", itemSet.getId(), itemSet.getDateTime());
    }

	private void handleLogItem(LogItem item, LogDefinitionItem itemDef, Date date) {
		logger.trace("Proccess log {} item with id.", item.getId());
		String deviceId = itemDef.getPath().getDeviceId();
		MeasurementRepresentation measurement = getMeasurement(new MeasurementKey(deviceId, date));
		measurement.setProperty(asFragmentName(itemDef), asFragment(item));
		logger.trace("Item with id {} processed.", item.getId());
	}

	private void saveMeasurements() {
	    for (Entry<MeasurementKey, MeasurementRepresentation> entry : measurements.entrySet()) {
	        MeasurementRepresentation measurement = entry.getValue();
	        String deviceId = entry.getKey().getDeviceId();
			SerialNumber deviceIdSerial = new SerialNumber(deviceId);
			try {
				ManagedObjectRepresentation source = inventoryRepository.findByExternalId(deviceIdSerial);
				measurement.setSource(source);
			} catch (SDKException ex) {
				logger.warn("Cannot find source for {}.", deviceIdSerial);
				continue;
			}
			logger.debug("Create measurement {}.", measurement);
			measurementApi.create(measurement);
        }
    }


	private static Map<String, BigDecimal> asFragment(LogItem logItem) {
		Map<String, BigDecimal> measurementValue = new HashMap<>();
		measurementValue.put("value", logItem.getValue());
	    return measurementValue;
    }
	
	private static String asFragmentName(LogDefinitionItem itemDef) {
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
}
