package com.cumulocity.agent.server.context.scope;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.cumulocity.agent.server.context.DeviceContext;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.sdk.client.notification.Subscriber;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;

public class ContextScopedSubscriber<I, M> implements Subscriber<I, M> {

    private final Subscriber<I, M> subscriber;

    private final DeviceContextService contextService;

    public ContextScopedSubscriber(Subscriber<I, M> subscriber, DeviceContextService service) {
        this.subscriber = subscriber;
        this.contextService = service;
    }

    public Subscription<I> subscribe(I channelID, SubscriptionListener<I, M> listener) {
        return subscriber.subscribe(channelID, contextSubscriptionListener(listener));
    }

    private ContextSubscriptionListener<I, M> contextSubscriptionListener(SubscriptionListener<I, M> subscriptionListener) {
        return new ContextSubscriptionListener<I, M>(contextService, contextService.getContext(), subscriptionListener);
    }

    @Override
    public void disconnect() {
        subscriber.disconnect();
    }

    static class ContextSubscriptionListener<I, M> implements SubscriptionListener<I, M> {

        private final Logger log = getLogger(ContextSubscriptionListener.class);

        private final SubscriptionListener<I, M> listener;

        private final DeviceContextService contextService;

        private final DeviceContext context;

        public ContextSubscriptionListener(DeviceContextService contextService, DeviceContext context,
                SubscriptionListener<I, M> paypalListener) {
            this.contextService = contextService;
            this.context = context;
            this.listener = paypalListener;
        }

        @Override
        public void onNotification(final Subscription<I> subscription, final M source) {
            executeWithnContext(new Runnable() {
                @Override
                public void run() {
                    try {
                        listener.onNotification(subscription, source);
                    } catch (Exception e) {
                        log.error(
                                "Failed deliver notification for tenant " + context.getLogin().getTenant() + "channel "
                                        + subscription.getObject() + " notification " + source, e);
                    }

                }
            });
        }

        private void executeWithnContext(Runnable runnable) {
            contextService.runWithinContext(context, runnable);
        }

        @Override
        public void onError(final Subscription<I> subscription, final Throwable ex) {
            executeWithnContext(new Runnable() {
                @Override
                public void run() {
                    log.warn("subscription failed for tenant " + context.getLogin().getTenant(), ex);
                    listener.onError(subscription, ex);
                }
            });

        }
    }

}
