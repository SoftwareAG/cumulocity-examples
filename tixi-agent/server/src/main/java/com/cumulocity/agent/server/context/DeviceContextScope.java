package com.cumulocity.agent.server.context;

import com.cumulocity.agent.server.context.scope.BaseScope;
import com.cumulocity.agent.server.context.scope.ScopeContainer;

public class DeviceContextScope extends BaseScope {

    private final DeviceContextService contextService;

    private final ScopeContainerRegistry registry;

    public DeviceContextScope(DeviceContextService contextService, ScopeContainerRegistry registry) {
        super(false);
        this.contextService = contextService;
        this.registry = registry;
    }

    @Override
    protected String getContextId() {
        return null;
    }

    @Override
    protected ScopeContainer getScopeContainer() {
        return registry.get(contextService.getContext());
    }
}
