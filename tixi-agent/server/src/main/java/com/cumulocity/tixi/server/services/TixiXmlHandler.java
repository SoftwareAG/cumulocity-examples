package com.cumulocity.tixi.server.services;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.IsDevice;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.repository.IdentityRepository;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.Agent;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.cumulocity.tixi.server.components.txml.TXMLUnmarshaller;
import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.txml.Log;
import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.cumulocity.tixi.server.model.txml.LogDefinitionItem;
import com.cumulocity.tixi.server.model.txml.LogDefinitionItemPath;
import com.cumulocity.tixi.server.model.txml.LogDefinitionItemSet;
import com.cumulocity.tixi.server.model.txml.LogItem;
import com.cumulocity.tixi.server.model.txml.LogItemSet;

@Component
public class TixiXmlHandler {

	private static final Logger logger = LoggerFactory.getLogger(TixiXmlHandler.class);

	private final TXMLUnmarshaller txmlUnmarshaller;
	private final DeviceContextService deviceContextService;
	private final IdentityRepository identityRepository;
	private final InventoryRepository inventoryRepository;
	private final MeasurementApi measurementApi;

	private final Map<String, LogDefinition> logDefinitions = new ConcurrentHashMap<String, LogDefinition>();

	@Autowired
	// @formatter:off
	public TixiXmlHandler(TXMLUnmarshaller txmlUnmarshaller, 
			DeviceContextService deviceContextService, 
			IdentityRepository identityRepository,
			InventoryRepository inventoryRepository,
			MeasurementApi measurementApi) {
	    this.txmlUnmarshaller = txmlUnmarshaller;
		this.deviceContextService = deviceContextService;
		this.identityRepository = identityRepository;
		this.inventoryRepository = inventoryRepository;
		this.measurementApi = measurementApi;
	// @formatter:on
	}

	public void handle(String fileName, Class<?> entityType) {
		logger.debug("Process fileName " + fileName + " with expected entity " + entityType);
		Object unmarshaled = txmlUnmarshaller.unmarshal(fileName, entityType);
		if (unmarshaled instanceof Log) {
			handle((Log) unmarshaled);
		} else if (unmarshaled instanceof LogDefinition) {
			handle((LogDefinition) unmarshaled);
		} else {
			throw new RuntimeException("Can't handle " + unmarshaled);
		}
	}

	private void handle(Log log) {
		String tenant = deviceContextService.getContext().getLogin().getTenant();
		LogDefinition logDefinition = logDefinitions.get(tenant);
		if(logDefinition == null) {
			logger.warn("There is no log definition specified for tenant {}; skip log", tenant);
			return;
		}
		String logDefinitionId = log.getId();
		for (LogItemSet logItemSet : log.getItemSets()) {
			Date date = logItemSet.getDateTime();
			for (LogItem logItem : logItemSet.getItems()) {
				LogDefinitionItem logDefinitionItem = logDefinition.getItem(logDefinitionId, logItem.getId());
				if(logDefinitionItem == null) {
					logger.warn("There is no log definition item for tenant: {}, " +
							"itemSetId: {} itemId: {}; skip this log.", tenant, logDefinitionId, logItem.getId());
					continue;
				}
				if(!isDevicePath(logDefinitionItem)) {
					logger.warn("Log definition item has no device path for tenant is not : {}, " +
							"itemSetId: {} itemId: {}; skip this log.", tenant, logDefinitionId, logItem.getId());					
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
	
	private void handle(LogDefinition logDefinition) {
		String tenant = deviceContextService.getContext().getLogin().getTenant();
		logger.info("Init log definitions for tenant {}.", tenant);
		logDefinitions.put(tenant, logDefinition);
		for (LogDefinitionItemSet itemSet : logDefinition.getItemSets().values()) {
			for (LogDefinitionItem logDefinitionItem : itemSet.getItems().values()) {
				if (isDevicePath(logDefinitionItem)) {
					handleDeviceItem(logDefinitionItem);
				}
			}
		}
	}

	private boolean isDevicePath(LogDefinitionItem logDefinitionItem) {
		return logDefinitionItem.getPath() != null && logDefinitionItem.getPath().getDeviceId() != null;
	}

	private void handleDeviceItem(LogDefinitionItem logDefinitionItem) {
		String agentId = logDefinitionItem.getPath().getDeviceId();
		SerialNumber agentSerial = new SerialNumber(agentId);
		GId agentGId = identityRepository.find(agentSerial);
		if(agentGId == null) {
			agentGId = registerAgent(agentSerial);
		}		
		String deviceId = logDefinitionItem.getPath().getDeviceId();
		SerialNumber deviceSerial = new SerialNumber(deviceId);
		GId deviceGId = identityRepository.find(deviceSerial);
		if(deviceGId == null) {
			deviceGId = registerDevice(agentGId, deviceSerial);
		}
	}

	private GId registerDevice(GId agentId, SerialNumber deviceSerial) {
		ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
		managedObjectRepresentation.set(new IsDevice());
		final ManagedObjectRepresentation managedObject = inventoryRepository.save(managedObjectRepresentation);
		identityRepository.save(managedObject.getId(), deviceSerial);
		inventoryRepository.bindToAgent(agentId, managedObject.getId());
		return managedObject.getId();
	}
	
	private GId registerAgent(SerialNumber agentSerial) {
		ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
		managedObjectRepresentation.set(new IsDevice());
		managedObjectRepresentation.set(new Agent());
		final ManagedObjectRepresentation managedObject = inventoryRepository.save(managedObjectRepresentation);
		identityRepository.save(managedObject.getId(), agentSerial);
		return managedObject.getId();
	}
}
