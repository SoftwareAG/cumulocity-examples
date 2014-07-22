package com.cumulocity.tixi.server.services.handler;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.tixi.server.services.DeviceService;

public abstract class TixiHandler implements InitializingBean {

    protected final DeviceContextService contextService;

    protected final DeviceService deviceService;

    protected final LogDefinitionRegister logDefinitionRegister;

    protected GId tixiAgentId;

    public TixiHandler(DeviceContextService contextService, DeviceService deviceService, LogDefinitionRegister logDefinitionRegister) {
        this.contextService = contextService;
        this.deviceService = deviceService;
        this.logDefinitionRegister = logDefinitionRegister;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        tixiAgentId = contextService.getCredentials().getDeviceId();
        Assert.notNull(tixiAgentId);
    }
	
	protected void setTixiAgentId(GId agentId) {
	    this.tixiAgentId = agentId;
	}
}
