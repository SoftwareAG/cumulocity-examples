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
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.tixi.server.model.txml.External;
import com.cumulocity.tixi.server.model.txml.External.Bus;
import com.cumulocity.tixi.server.model.txml.External.Device;
import com.cumulocity.tixi.server.services.DeviceService;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TixiExternalHandler extends TixiHandler {

	private static final Logger logger = LoggerFactory.getLogger(TixiExternalHandler.class);
	
	private final Map<String, ManagedObjectRepresentation> persistedAgents = new HashMap<>();
	private final Map<String, ManagedObjectRepresentation> persistedDevices = new HashMap<>();

	@Autowired
    public TixiExternalHandler(DeviceContextService contextService, DeviceService deviceService, LogDefinitionRegister logDefinitionRegister) {
        super(contextService, deviceService, logDefinitionRegister);
    }

	public void handle(External external) {
		logger.info("Process external file.");
		for (Bus bus : external.getBuses()) {
			logger.info("Process bus {}.", bus);
			for (Device device : bus.getDevices()) {
				handleDevice(bus, device);
			}
			logger.info("External bus {} processed.", bus);
		}
		logger.info("External file processed.");
	}

	private void handleDevice(Bus bus, Device device) {
		logger.debug("Process external device: {} on bus: {}.", device, bus);
		String agentId = bus.getName();
		ManagedObjectRepresentation agentRep = persistedAgents.get(agentId);
		if (agentRep == null) {
			agentRep = deviceService.saveAgentIfNotExists(agentId, bus.getName());
			persistedAgents.put(agentId, agentRep);
		}
		String deviceId = device.getName();
		ManagedObjectRepresentation deviceRep = persistedDevices.get(deviceId);
		if (deviceRep == null) {
			deviceRep = deviceService.saveDeviceIfNotExists(deviceId, device.getName(), agentRep.getId());
			persistedDevices.put(deviceId, deviceRep);
		}
		logger.debug("Device processed.");
	}

}
