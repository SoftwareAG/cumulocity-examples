package com.cumulocity.agent.server.context;

import com.cumulocity.agent.server.context.scope.ScopeContainer;

public class ContextScopeContainerRegistry implements ScopeContainerRegistry {

    @Override
    public ScopeContainer get(DeviceContext context) {
        return context.getScope();
    }

}
