package com.cumulocity.tixi.server.services.handler;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.cumulocity.tixi.server.model.txml.LogDefinitionItem;

public abstract class TixiHandler implements InitializingBean {
	
	protected final DeviceContextService contextService;
	protected final InventoryRepository inventoryRepository;
	protected final MeasurementApi measurementApi;
	protected final LogDefinitionRegister logDefinitionRegister;
	protected GId tixiAgentId;

	public TixiHandler(DeviceContextService contextService, InventoryRepository inventoryRepository,
            MeasurementApi measurementApi, LogDefinitionRegister logDefinitionRegister) {
	    this.contextService = contextService;
	    this.inventoryRepository = inventoryRepository;
	    this.measurementApi = measurementApi;
	    this.logDefinitionRegister = logDefinitionRegister;
    }
	
	@Override
    public void afterPropertiesSet() throws Exception {
		tixiAgentId = contextService.getCredentials().getDeviceId();
		Assert.notNull(tixiAgentId);
    }
	
	protected boolean isDevicePath(LogDefinitionItem logDefinitionItem) {
		return logDefinitionItem.getPath() != null && logDefinitionItem.getPath().getDeviceId() != null;
	}


}
