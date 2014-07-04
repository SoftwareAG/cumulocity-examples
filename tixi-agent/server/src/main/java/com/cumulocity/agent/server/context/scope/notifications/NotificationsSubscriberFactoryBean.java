package com.cumulocity.agent.server.context.scope.notifications;

import static com.google.common.cache.CacheBuilder.newBuilder;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.FactoryBean;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.sdk.client.notification.Subscriber;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public final class NotificationsSubscriberFactoryBean<I, M> implements FactoryBean<Subscriber<I, M>> {

    private static final int CONCURRENCY_LEVEL = 1;

    private final DeviceContextService contextService;

    private final LoadingCache<String, Subscriber<I, M>> cache;

    public NotificationsSubscriberFactoryBean(final DeviceContextService contextService, final Supplier<Subscriber<I, M>> supplier) {
        this.contextService = contextService;
        this.cache = newBuilder().concurrencyLevel(CONCURRENCY_LEVEL).removalListener(new DisconnectOnRemoval<I, M>()).build(CacheLoader.from(supplier));
    }

    @Override
    public Subscriber<I, M> getObject() throws Exception {
        return cache.get(contextService.getCredentials().getTenant());
    }

    @Override
    public Class<?> getObjectType() {
        return Subscriber.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @PreDestroy
    public void dispose() {
        cache.invalidateAll();
        cache.cleanUp();
    }

    private static final class DisconnectOnRemoval<I, M> implements RemovalListener<String, Subscriber<I, M>> {
        @Override
        public void onRemoval(RemovalNotification<String, Subscriber<I, M>> notification) {
            notification.getValue().disconnect();
        }
    }
}
