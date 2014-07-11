package com.cumulocity.agent.server.context;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.cumulocity.agent.server.context.scope.BaseScope;
import com.cumulocity.agent.server.context.scope.ScopeContainer;

public class DeviceContextScope extends BaseScope implements InitializingBean {

    public static final String CONTEXT_SCOPE = "context";

    private DeviceContextService contextService;

    public DeviceContextScope() {
        super(false);
    }

    public DeviceContextScope(DeviceContextService contextService) {
        this();
        this.contextService = contextService;
    }

    public void setContextService(DeviceContextService contextService) {
        this.contextService = contextService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(contextService, "ContextService cannot be null!");
    }

    @Override
    protected String getContextId() {
        return null;
    }

    @Override
    protected ScopeContainer getScopeContainer() {
        return contextService.getContext().getScope();
    }
}
