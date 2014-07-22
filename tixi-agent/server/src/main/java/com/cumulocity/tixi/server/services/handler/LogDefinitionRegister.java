package com.cumulocity.tixi.server.services.handler;

import org.springframework.stereotype.Component;

import c8y.inject.DeviceScope;

import com.cumulocity.tixi.server.model.txml.LogDefinition;

@Component
@DeviceScope
public class LogDefinitionRegister {

	private LogDefinition logDefinition = null;

	public void register(LogDefinition logDefinition) {
		this.logDefinition = logDefinition; 
	}

	public LogDefinition getLogDefinition() {
		return logDefinition;
	}

}
