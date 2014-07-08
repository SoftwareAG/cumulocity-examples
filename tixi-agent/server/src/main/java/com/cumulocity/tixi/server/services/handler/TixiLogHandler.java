package com.cumulocity.tixi.server.services.handler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.cumulocity.tixi.server.model.txml.LogDefinitionItemPath;
import com.cumulocity.tixi.server.model.txml.LogItem;
import com.cumulocity.tixi.server.model.txml.LogItemSet;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TixiLogHandler extends TixiHandler<Log> {
	
	private static final Logger logger = LoggerFactory.getLogger(TixiLogHandler.class);

	public TixiLogHandler(DeviceContextService deviceContextService, IdentityRepository identityRepository, InventoryRepository inventoryRepository,
            MeasurementApi measurementApi, LogDefinitionRegister logDefinitionRegister) {
	    super(deviceContextService, identityRepository, inventoryRepository, measurementApi, logDefinitionRegister);
    }

	@Override
	public void handle(Log log) {
		LogDefinition logDefinition = logDefinitionRegister.getLogDefinition();
		if(logDefinition == null) {
			return;
		}
		String logDefinitionId = log.getId();
		for (LogItemSet logItemSet : log.getItemSets()) {
			Date date = logItemSet.getDateTime();
			for (LogItem logItem : logItemSet.getItems()) {
				LogDefinitionItem logDefinitionItem = logDefinition.getItem(logDefinitionId, logItem.getId());
				if(logDefinitionItem == null) {
					logger.warn("There is no log definition item for itemSetId: {}," +
							" itemId: {}; skip this log.", logDefinitionId, logItem.getId());
					continue;
				}
				if(!isDevicePath(logDefinitionItem)) {
					logger.warn("Log definition item has no device path variable " +
							"itemSetId: {} itemId: {}; skip this log.", logDefinitionId, logItem.getId());					
				}
				
				handleLogItem(date, logDefinitionItem, logItem);
            }
        }
	}

	private void handleLogItem(Date date, LogDefinitionItem logDefinitionItem, LogItem logItem) {
		LogDefinitionItemPath path = logDefinitionItem.getPath();
		String deviceId = path.getDeviceId();
		MeasurementRepresentation measurement = new MeasurementRepresentation();
		measurement.setTime(date);
		ManagedObjectRepresentation source;
		SerialNumber deviceIdSerial = new SerialNumber(deviceId);
		try {
			source = inventoryRepository.findByExternalId(deviceIdSerial);
		} catch (SDKException ex) {
			logger.warn("Cannot find source for {}.", deviceIdSerial);
			return;
		}
		measurement.setSource(source);
		MeasurementValue measurementValue = new MeasurementValue();
		measurementValue.setValue(logItem.getValue());
		measurement.setProperty("c8y_" + path.getName(), measurementValue);
		measurementApi.create(measurement);
	}
	


}
