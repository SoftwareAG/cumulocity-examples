package com.cumulocity.tixi.server.services;

import static com.google.common.cache.CacheBuilder.newBuilder;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;

@Component
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
