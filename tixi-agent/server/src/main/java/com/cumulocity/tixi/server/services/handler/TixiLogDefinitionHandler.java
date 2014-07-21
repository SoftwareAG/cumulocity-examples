package com.cumulocity.tixi.server.services.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.cumulocity.tixi.server.model.txml.RecordItemDefinition;
import com.cumulocity.tixi.server.model.txml.RecordItemPath;
import com.cumulocity.tixi.server.model.txml.RecordDefinition;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TixiLogDefinitionHandler extends TixiHandler {

	private static final Logger logger = LoggerFactory.getLogger(TixiLogDefinitionHandler.class);

	private final Map<SerialNumber, ManagedObjectRepresentation> persistedAgents = new HashMap<>();
	private final Map<SerialNumber, ManagedObjectRepresentation> persistedDevices = new HashMap<>();

	@Autowired
	public TixiLogDefinitionHandler(DeviceContextService deviceContextService, InventoryRepository inventoryRepository,
	        MeasurementApi measurementApi, LogDefinitionRegister logDefinitionRegister) {
		super(deviceContextService, inventoryRepository, measurementApi, logDefinitionRegister);
	}

	public void handle(LogDefinition logDefinition) {
		logger.info("Process log definition.");
		logDefinitionRegister.register(logDefinition);
		for (RecordDefinition itemSet : logDefinition.getRecordDefinitions().values()) {
			logger.info("Process log definition item set with id {}", itemSet.getId());
			for (RecordItemDefinition logDefinitionItem : itemSet.getRecordItemDefinitions().values()) {
				if (isDevicePath(logDefinitionItem)) {
					handleDeviceItem(logDefinitionItem);
				}
			}
			logger.info("Log definition item set with id {} processed.", itemSet.getId());
		}
		logger.info("Log definition processed.");
	}

	private void handleDeviceItem(RecordItemDefinition logDefinitionItem) {
		logger.debug("Process log definition item: {}", logDefinitionItem);
		RecordItemPath path = logDefinitionItem.getPath();
		SerialNumber agentSerial = new SerialNumber(path.getAgentId());
		ManagedObjectRepresentation agent = persistedAgents.get(agentSerial);
		if (agent == null) {
			agent = inventoryRepository.saveAgentIfNotExists(agentSerial.getValue(), agentSerial.getValue(), agentSerial, agentId);
			persistedAgents.put(agentSerial, agent);
		}
		SerialNumber deviceSerial = new SerialNumber(path.getDeviceId());
		ManagedObjectRepresentation device = persistedDevices.get(deviceSerial);
		if (device == null) {
			device = inventoryRepository.saveDeviceIfNotExists(deviceSerial, agent.getId());
			persistedDevices.put(deviceSerial, device);
		}
		logger.debug("Log definition item processed.");
	}
}
