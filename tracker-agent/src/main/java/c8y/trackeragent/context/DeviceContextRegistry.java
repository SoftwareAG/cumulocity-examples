/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.context;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class DeviceContextRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DeviceContextRegistry.class);
    
    private static final DeviceContextRegistry instance = new DeviceContextRegistry();

    private final LoadingCache<String, DeviceContext> content = CacheBuilder.newBuilder().build(new CacheLoader<String, DeviceContext>() {

        @Override
        public DeviceContext load(String arg0) throws Exception {
            return new DeviceContext();
        }

    });
    
    public static DeviceContextRegistry get() {
        return instance;
    }
    
    private DeviceContextRegistry() {}

    public DeviceContext get(String imei) {
        try {
            return content.get(imei);
        } catch (ExecutionException e) {
            // should never happen
            logger.error("Error loading for imei " + imei, e);
            return null;
        }
    }

}
