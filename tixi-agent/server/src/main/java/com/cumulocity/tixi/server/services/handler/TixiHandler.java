package com.cumulocity.tixi.server.services.handler;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.repository.IdentityRepository;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.cumulocity.tixi.server.model.txml.LogDefinitionItem;

public abstract class TixiHandler<T> {
	
	protected final DeviceContextService deviceContextService;
	protected final IdentityRepository identityRepository;
	protected final InventoryRepository inventoryRepository;
	protected final MeasurementApi measurementApi;
	protected final LogDefinitionRegister logDefinitionRegister;

	public TixiHandler(DeviceContextService deviceContextService, IdentityRepository identityRepository, InventoryRepository inventoryRepository,
            MeasurementApi measurementApi, LogDefinitionRegister logDefinitionRegister) {
	    this.deviceContextService = deviceContextService;
	    this.identityRepository = identityRepository;
	    this.inventoryRepository = inventoryRepository;
	    this.measurementApi = measurementApi;
	    this.logDefinitionRegister = logDefinitionRegister;
    }

	public abstract void handle(T element);
	
	protected boolean isDevicePath(LogDefinitionItem logDefinitionItem) {
		return logDefinitionItem.getPath() != null && logDefinitionItem.getPath().getDeviceId() != null;
	}


}
