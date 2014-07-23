package com.cumulocity.agent.server.context;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;

public class DeviceContextServiceImpl implements DeviceContextService {

    private final Logger log = LoggerFactory.getLogger(DeviceContextServiceImpl.class);

    private final ThreadLocal<DeviceContext> localContext = new NamedThreadLocal<DeviceContext>("deviceLocalContext");

    @Override
    public DeviceContext getContext() {
        DeviceContext context = doGetContext();
        if (context == null) {
            throw new IllegalStateException("Not within any context!");
        }
        return context;
    }

    private DeviceContext doGetContext() {
        return localContext.get();
    }

    @Override
    public DeviceCredentials getCredentials() {
        return getContext().getLogin();
    }

    @Override
    public void runWithinContext(DeviceContext context, Runnable task) {
        try {
            callWithinContext(context, new CallableRunnableWrapper(task));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <V> V callWithinContext(DeviceContext context, Callable<V> task) throws Exception {
        DeviceContext previousContext = doGetContext();
        enterContext(context);
        log.debug("entering to  {} ", context);
        try {
            return task.call();
        } catch (Exception e) {
            log.warn("execution of task failed within tenant : " + context.getLogin().getTenant());
            throw e;
        } finally {
            log.debug("leaving from {} ", context);
            leaveContext(previousContext);
        }
    }

    private void enterContext(DeviceContext newContext) {
        DeviceContext contextCopy = new DeviceContext(newContext.getLogin());
        localContext.set(contextCopy);
    }

    private void leaveContext(DeviceContext previousContext) {
        if (previousContext == null) {
            localContext.remove();
        } else {
            localContext.set(previousContext);
        }
    }

    private static class CallableRunnableWrapper implements Callable<Void> {

        private final Runnable runnable;

        public CallableRunnableWrapper(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public Void call() throws Exception {
            runnable.run();
            return null;
        }
    }

    @Override
    public Runnable withinContext(final DeviceContext context, final Runnable task) {
        return new Runnable() {
            @Override
            public void run() {
                runWithinContext(context, task);
            }
        };
    }

    @Override
    public <V> Callable<V> withinContext(final DeviceContext context, final Callable<V> task) {
        return new Callable<V>() {
            @Override
            public V call() throws Exception {
                return callWithinContext(context, task);
            }
        };
    }

    @Override
    public Runnable withinContext(Runnable task) {
        return withinContext(getContext(), task);
    }

    @Override
    public <V> Callable<V> withinContext(Callable<V> task) {
        return withinContext(getContext(), task);
    }
}
