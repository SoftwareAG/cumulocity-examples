package com.cumulocity.tixi.server.services.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import c8y.IsDevice;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.repository.IdentityRepository;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.Agent;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.cumulocity.tixi.server.model.SerialNumber;
import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.cumulocity.tixi.server.model.txml.LogDefinitionItem;
import com.cumulocity.tixi.server.model.txml.LogDefinitionItemSet;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TixiLogDefinitionHandler extends TixiHandler<LogDefinition> {

	private static final Logger logger = LoggerFactory.getLogger(TixiLogDefinitionHandler.class);

	private final Map<SerialNumber, ManagedObjectRepresentation> persistedAgents = new HashMap<>();
	private final Map<SerialNumber, ManagedObjectRepresentation> persistedDevices = new HashMap<>();

	@Autowired
	public TixiLogDefinitionHandler(DeviceContextService deviceContextService, IdentityRepository identityRepository, InventoryRepository inventoryRepository,
	        MeasurementApi measurementApi, LogDefinitionRegister logDefinitionRegister) {
		super(deviceContextService, identityRepository, inventoryRepository, measurementApi, logDefinitionRegister);
	}

	public void handle(LogDefinition logDefinition) {
		logger.info("Process log definition.");
		logDefinitionRegister.register(logDefinition);
		for (LogDefinitionItemSet itemSet : logDefinition.getItemSets().values()) {
			logger.info("Process log definition item set with id {}", itemSet.getId());
			for (LogDefinitionItem logDefinitionItem : itemSet.getItems().values()) {
				if (isDevicePath(logDefinitionItem)) {
					handleDeviceItem(logDefinitionItem);
				}
			}
			logger.info("Log definition item set with id {} processed.", itemSet.getId());
		}
		logger.info("Log definition processed.");
	}

	private void handleDeviceItem(LogDefinitionItem logDefinitionItem) {
		logger.debug("Process log definition item: {}", logDefinitionItem);
		String agentId = logDefinitionItem.getPath().getAgentId();
		SerialNumber agentSerial = new SerialNumber(agentId);
		ManagedObjectRepresentation agent = persistedAgents.get(agentSerial);
		if (agent == null) {
			agent = findMoOrNull(agentSerial);
			if (agent == null) {
				agent = registerAgent(agentSerial);
			}
			persistedAgents.put(agentSerial, agent);
		}
		String deviceId = logDefinitionItem.getPath().getDeviceId();
		SerialNumber deviceSerial = new SerialNumber(deviceId);
		ManagedObjectRepresentation device = persistedDevices.get(deviceSerial);
		if (device == null) {
			device = findMoOrNull(deviceSerial);
			if (device == null) {
				device = registerDevice(agent.getId(), deviceSerial);
			}
			persistedDevices.put(deviceSerial, device);
		}
		logger.debug("Log definition item processed.");
	}

	private ManagedObjectRepresentation findMoOrNull(SerialNumber agentSerial) {
		try {
			return inventoryRepository.findByExternalId(agentSerial);
		} catch (SDKException sdkEx) {
			return null;
		}
	}

	private ManagedObjectRepresentation registerDevice(GId agentId, SerialNumber deviceSerial) {
		logger.debug("Register device: {}", deviceSerial);
		ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
		managedObjectRepresentation.set(new IsDevice());
		return inventoryRepository.save(managedObjectRepresentation, deviceSerial);
	}

	private ManagedObjectRepresentation registerAgent(SerialNumber agentSerial) {
		logger.debug("Register agent: {}", agentSerial);
		ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
		managedObjectRepresentation.set(new IsDevice());
		managedObjectRepresentation.set(new Agent());
		return inventoryRepository.save(managedObjectRepresentation, agentSerial);
	}
}
