package com.cumulocity.agent.server.context;

import com.cumulocity.agent.server.context.scope.DefaultScopeContainer;
import com.cumulocity.agent.server.context.scope.ScopeContainer;

public class DeviceContext {

    private final DeviceCredentials login;

    private final ScopeContainer scope;

    public DeviceContext(DeviceCredentials login) {
        this.login = login;
        this.scope = new DefaultScopeContainer();
    }

    public DeviceCredentials getLogin() {
        return login;
    }

    public ScopeContainer getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return "DeviceContext [login=" + login + "]";
    }
    
    
}
