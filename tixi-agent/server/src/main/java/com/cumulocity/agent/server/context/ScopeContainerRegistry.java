package com.cumulocity.agent.server.context;

import com.cumulocity.agent.server.context.scope.ScopeContainer;

public interface ScopeContainerRegistry {
    ScopeContainer get(DeviceContext context);
}
