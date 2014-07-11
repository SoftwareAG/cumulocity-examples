package com.cumulocity.agent.server.context.scope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import com.cumulocity.agent.server.context.scope.KeyBasedLocksMap.KeyBasedLock;

public abstract class BaseScope implements Scope {

    protected static final Logger LOG = LoggerFactory.getLogger(BaseScope.class);

    private final KeyBasedLocksMap locks = new KeyBasedLocksMap();

    private final boolean sync;

    protected BaseScope(boolean sync) {
        this.sync = sync;
    }

    protected abstract String getContextId();

    protected abstract ScopeContainer getScopeContainer();

    @Override
    public Object get(final String name, final ObjectFactory<?> objectFactory) {
        if (sync) {
            return doGetSynchronized(name, objectFactory);
        } else {
            return doGet(name, objectFactory);
        }
    }

    private Object doGetSynchronized(String name, ObjectFactory<?> objectFactory) {
        // synchronization is done only on request for the same contextId and bean name
        KeyBasedLock lock = locks.lockForKeyElements(getContextId(), name);
        try {
            return doGet(name, objectFactory);
        } finally {
            lock.unlock();
        }
    }

    private Object doGet(String name, ObjectFactory<?> objectFactory) {
        ScopeContainer scopeContainer = getScopeContainer();
        if (scopeContainer.contains(name)) {
            return getExisting(scopeContainer, name);
        } else {
            return createNew(scopeContainer, name, objectFactory);
        }
    }

    private Object getExisting(ScopeContainer container, String name) {
        Object scoped = container.getObject(name);
        LOG.trace("Returned existing scoped instance of bean '{}' for '{}'.", name, getContextId());
        return scoped;
    }

    private Object createNew(ScopeContainer container, String name, ObjectFactory<?> objectFactory) {
        Object scoped = getObjectFromFactory(objectFactory);
        container.putObject(name, scoped);
        LOG.trace("Created new scoped instance of bean '{}' for '{}'.", name, getContextId());
        return scoped;
    }

    @Override
    public Object remove(final String name) {
        LOG.trace("Removing tenant scoped instance of bean '{}' for tenant '{}'.", name, getContextId());
        return getScopeContainer().removeObject(name);
    }

    @Override
    public void registerDestructionCallback(final String name, final Runnable callback) {
        LOG.trace("Registering destruction callback for tenant scoped bean '{}' for tenant '{}'.", name, getContextId());
        getScopeContainer().addDestructionCallback(name, callback);
    }

    @Override
    public Object resolveContextualObject(final String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return getContextId();
    }

    protected Object getObjectFromFactory(final ObjectFactory<?> objectFactory) {
        return objectFactory.getObject();
    }
}
