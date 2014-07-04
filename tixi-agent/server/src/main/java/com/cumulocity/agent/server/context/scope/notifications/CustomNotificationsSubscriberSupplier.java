package com.cumulocity.agent.server.context.scope.notifications;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.sdk.client.cep.CepApi;
import com.cumulocity.sdk.client.notification.Subscriber;
import com.google.common.base.Supplier;

public final class CustomNotificationsSubscriberSupplier implements Supplier<Subscriber<String, Object>> {
    private final DeviceContextService contextService;

    private final CepApi cepApi;

    public CustomNotificationsSubscriberSupplier(DeviceContextService contextService, CepApi cepApi) {
        this.contextService = contextService;
        this.cepApi = cepApi;
    }

    @Override
    public Subscriber<String, Object> get() {
        return new ContextScopedSubscriber<String, Object>(cepApi.getCustomNotificationsSubscriber(), contextService);
    }
}