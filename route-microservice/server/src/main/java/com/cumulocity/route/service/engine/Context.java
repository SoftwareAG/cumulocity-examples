package com.cumulocity.route.service.engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class Context {

    private final Map<Object, Object> firstEventForPairFrom = new HashMap<>();
    private final Map<Class, Object> lastEventOfType = new HashMap<>();
    private final Map<Class, Object> firstEventOfType = new HashMap<>();
    private final Object source;

    public Object removeFirstEvent(Object key) {
        return firstEventForPairFrom.remove(key);
    }

    public boolean containsFirstEvent(Object key) {
        return firstEventForPairFrom.containsKey(key);
    }

    public void putFirstEvent(Object key, Object event) {
        firstEventForPairFrom.put(key, event);
    }

    public <T> Optional<T> getLast(Class<T> filter) {
        return Optional.ofNullable((T) lastEventOfType.get(filter));
    }

    public <T> Optional<T> getFirst(Class<T> filter) {
        return Optional.ofNullable((T) firstEventOfType.get(filter));
    }

    public <T> void setCurrent(Object event) {
        final Class<?> key = event.getClass();
        firstEventOfType.putIfAbsent(key, event);
        lastEventOfType.put(key, event);
    }
}
