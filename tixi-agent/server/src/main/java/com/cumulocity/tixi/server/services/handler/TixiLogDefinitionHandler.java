package com.cumulocity.tixi.server.services.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.cumulocity.tixi.server.services.DeviceService;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TixiLogDefinitionHandler extends TixiHandler {

	private static final Logger logger = LoggerFactory.getLogger(TixiLogDefinitionHandler.class);

	@Autowired
	public TixiLogDefinitionHandler(DeviceContextService deviceContextService, DeviceService deviceService,
	         LogDefinitionRegister logDefinitionRegister) {
		super(deviceContextService, deviceService,  logDefinitionRegister);
	}

	public void handle(LogDefinition logDefinition) {
		logger.info("Process log definition.");
		logDefinitionRegister.register(logDefinition);
	}
}
