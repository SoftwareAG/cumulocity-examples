package com.cumulocity.agent.server.context;

import java.util.concurrent.Callable;

public interface DeviceContextService {

    DeviceContext getContext();

    DeviceCredentials getCredentials();

    void runWithinContext(DeviceContext context, Runnable task);

    <V> V callWithinContext(DeviceContext context, Callable<V> task) throws Exception;
    
    Runnable withinContext(DeviceContext context, Runnable task);
    
    <V> Callable<V> withinContext(DeviceContext context, Callable<V> task);
    
    Runnable withinContext(Runnable task);
    
    <V> Callable<V> withinContext(Callable<V> task);

}
