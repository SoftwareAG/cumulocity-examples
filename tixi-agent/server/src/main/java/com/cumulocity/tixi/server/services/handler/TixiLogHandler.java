package com.cumulocity.tixi.server.services.handler;

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
import com.cumulocity.agent.server.repository.IdentityRepository;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.measurement.MeasurementValue;
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

	@Autowired
	public TixiLogHandler(DeviceContextService deviceContextService, IdentityRepository identityRepository, InventoryRepository inventoryRepository,
            MeasurementApi measurementApi, LogDefinitionRegister logDefinitionRegister) {
	    super(deviceContextService, identityRepository, inventoryRepository, measurementApi, logDefinitionRegister);
    }
	
	private Map<MeasurementKey, MeasurementRepresentation> measurements = new HashMap<>();
	private LogDefinition logDefinition;
	private Log log;

	@Override
	public void handle(Log log) {
		this.log = log;
		logDefinition = logDefinitionRegister.getLogDefinition();
		if(logDefinition == null) {
			return;
		}
		for (LogItemSet itemSet : log.getItemSets()) {
			handleItemSet(itemSet);
        }
		saveMeasurements();
	}

	private void handleItemSet(LogItemSet itemSet) {
		String logDefinitionId = log.getId();
	    for (LogItem item : itemSet.getItems()) {
	    	LogDefinitionItem itemDef = getLogDefinitionItem(item);
	    	if(itemDef == null) {
	    		logger.warn("There is no log definition item for itemSetId: {}," +
	    				" itemId: {}; skip this log.", logDefinitionId, item.getId());
	    		continue;
	    	}
	    	if(!isDevicePath(itemDef)) {
	    		logger.warn("Log definition item has no device path variable " +
	    				"itemSetId: {} itemId: {}; skip this log.", logDefinitionId, item.getId());
	    		continue;
	    	}
	    	
	    	handleLogItem(item, itemDef, itemSet.getDateTime());
	    }
    }

	private LogDefinitionItem getLogDefinitionItem(LogItem logItem) {
	    return logDefinition.getItem(log.getId(), logItem.getId());
    }
	
	private void handleLogItem(LogItem item, LogDefinitionItem itemDef, Date date) {
		String deviceId = itemDef.getPath().getDeviceId();
		MeasurementRepresentation measurement = getMeasurement(new MeasurementKey(deviceId, date));
		measurement.setProperty(asFragmentName(itemDef), asFragment(item));
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
			measurementApi.create(measurement);
        }
    }


	private static MeasurementValue asFragment(LogItem logItem) {
	    MeasurementValue measurementValue = new MeasurementValue();
		measurementValue.setValue(logItem.getValue());
	    return measurementValue;
    }
	
	private static String asFragmentName(LogDefinitionItem itemDef) {
	    return "c8y_" + itemDef.getPath().getName();
    }
		
	private MeasurementRepresentation getMeasurement(MeasurementKey key) {
		MeasurementRepresentation result = measurements.get(key);
		if(result == null) {
			result = new MeasurementRepresentation();
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
