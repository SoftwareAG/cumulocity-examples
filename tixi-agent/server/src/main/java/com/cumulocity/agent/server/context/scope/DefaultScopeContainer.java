package com.cumulocity.agent.server.context.scope;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Implements a scope container.
 * @author Darek Kaczynski
 */
public class DefaultScopeContainer implements ScopeContainer {

    private final ConcurrentMap<String, Object> objectsMap;

    private final ConcurrentMap<String, List<Runnable>> callbacksMap;

    private final ConcurrentMap<String, Object> objectsInDestruction;

    private volatile boolean containerInDestruction;

    public DefaultScopeContainer() {
        this.objectsMap = new ConcurrentHashMap<String, Object>();
        this.callbacksMap = new ConcurrentHashMap<String, List<Runnable>>();
        this.objectsInDestruction = new ConcurrentHashMap<String, Object>();
        this.containerInDestruction = false;
    }

    @Override
    public Set<String> getObjectNames() {
        ensureContainerNotInDestruction();
        return objectsMap.keySet();
    }

    @Override
    public boolean contains(String name) {
        ensureObjectNotInDestruction(name);
        return objectsMap.containsKey(name);
    }

    @Override
    public Object getObject(String name) {
        ensureObjectNotInDestruction(name);
        return objectsMap.get(name);
    }

    @Override
    public void putObject(String name, Object obj) {
        ensureObjectNotInDestruction(name);
        Object previous = objectsMap.putIfAbsent(name, obj);
        if (previous != null) {
            throw new IllegalArgumentException(format("Object with name %s is already present in the container!", name));
        }
    }

    @Override
    public Object removeObject(String name) {
        ensureContainerNotInDestruction();
        return doRemoveObject(name);
    }

    @Override
    public void addDestructionCallback(String name, Runnable callback) {
        ensureObjectNotInDestruction(name);
        List<Runnable> list = callbacksMap.get(name);
        if (list == null) {
            list = new ArrayList<Runnable>();
            callbacksMap.put(name, list);
        }
        list.add(callback);
    }

    @Override
    public void clear() {
        ensureContainerNotInDestruction();
        containerInDestruction = true;
        try {
            for (String name : objectsMap.keySet()) {
                doRemoveObject(name);
            }
            objectsMap.clear();
            callbacksMap.clear();
        } finally {
            containerInDestruction = false;
        }
    }

    private Object doRemoveObject(String name) {
        if (objectsInDestruction.putIfAbsent(name, name) != null) {
            throw new IllegalStateException("The object is currenlty in destruction!");
        }
        try {
            Object removed = objectsMap.remove(name);
            runDestructionCallbacks(name);
            return removed;
        } finally {
            objectsInDestruction.remove(name);
        }
    }

    private void runDestructionCallbacks(String name) {
        List<Runnable> list = callbacksMap.remove(name);
        if (list == null) {
            return;
        }
        for (Runnable callback : list) {
            callback.run();
        }
    }

    private void ensureContainerNotInDestruction() {
        if (containerInDestruction) {
            throw new IllegalStateException("The scope container is currenlty in destruction!");
        }
    }

    private void ensureObjectNotInDestruction(String name) {
        ensureContainerNotInDestruction();
        if (objectsInDestruction.containsKey(name)) {
            throw new IllegalStateException("The object is currenlty in destruction!");
        }
    }
}
