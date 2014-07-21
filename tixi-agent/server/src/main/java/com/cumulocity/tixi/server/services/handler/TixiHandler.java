package com.cumulocity.tixi.server.services.handler;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.cumulocity.tixi.server.model.txml.LogDefinitionItem;

public abstract class TixiHandler<T> implements InitializingBean {
	
	protected final DeviceContextService contextService;
	protected final InventoryRepository inventoryRepository;
	protected final MeasurementApi measurementApi;
	protected final LogDefinitionRegister logDefinitionRegister;
	protected GId agentId;

	public TixiHandler(DeviceContextService contextService, InventoryRepository inventoryRepository,
            MeasurementApi measurementApi, LogDefinitionRegister logDefinitionRegister) {
	    this.contextService = contextService;
	    this.inventoryRepository = inventoryRepository;
	    this.measurementApi = measurementApi;
	    this.logDefinitionRegister = logDefinitionRegister;
    }
	
	@Override
    public void afterPropertiesSet() throws Exception {
		agentId = contextService.getCredentials().getDeviceId();
		Assert.notNull(agentId);
    }
	
	public abstract void handle(T element);
	
	protected boolean isDevicePath(LogDefinitionItem logDefinitionItem) {
		return logDefinitionItem.getPath() != null && logDefinitionItem.getPath().getDeviceId() != null;
	}


}
