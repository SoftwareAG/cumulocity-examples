package com.cumulocity.tixi.server.services.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.tixi.server.model.txml.LogDefinition;

@Component
public class LogDefinitionRegister {

	private static final Logger logger = LoggerFactory.getLogger(LogDefinitionRegister.class);

	private final DeviceContextService deviceContextService;

	private final Map<String, LogDefinition> logDefinitions = new ConcurrentHashMap<String, LogDefinition>();

	@Autowired
	public LogDefinitionRegister(DeviceContextService deviceContextService) {
		this.deviceContextService = deviceContextService;
	}

	public void register(LogDefinition logDefinition) {
		String tenant = deviceContextService.getContext().getLogin().getTenant();
		logger.info("Init log definitions for tenant {}.", tenant);
		logDefinitions.put(tenant, logDefinition);
	}

	public LogDefinition getLogDefinition() {
		String tenant = deviceContextService.getContext().getLogin().getTenant();
		LogDefinition result = logDefinitions.get(tenant);
		if (result == null) {
			logger.warn("There is no log definition specified for tenant {}; skip log", tenant);
		}
		return result;
	}

}
