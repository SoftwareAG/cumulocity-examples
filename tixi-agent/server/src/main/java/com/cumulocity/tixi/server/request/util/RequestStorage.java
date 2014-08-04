package com.cumulocity.tixi.server.request.util;

import static com.google.common.cache.CacheBuilder.newBuilder;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import c8y.inject.DeviceScope;

import com.google.common.cache.Cache;

@Component
@DeviceScope
public class RequestStorage {
    
    private Cache<String, Class<?>> cache;
    
    public RequestStorage() {
        this.cache = newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();
    }
    
    public void put(String requestId, Class<?> entityType) {
        cache.put(requestId, entityType);
    }
    
    public Class<?> get(String requestId) {
        Class<?> entityType = cache.getIfPresent(requestId);
        if (entityType != null) {
            cache.invalidate(requestId);
        }
        return entityType;
    }

}
