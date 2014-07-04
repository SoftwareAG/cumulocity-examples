package com.cumulocity.agent.server.context.scope.notifications;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.notification.Subscriber;
import com.google.common.base.Supplier;

public final class DeviceControlNotificationsSubscriberSupplier implements Supplier<Subscriber<GId, OperationRepresentation>> {
    private final DeviceContextService contextService;

    private final DeviceControlApi deviceControlApi;

    public DeviceControlNotificationsSubscriberSupplier(DeviceContextService contextService, DeviceControlApi deviceControlApi) {
        this.contextService = contextService;
        this.deviceControlApi = deviceControlApi;
    }

    @Override
    public Subscriber<GId, OperationRepresentation> get() {
        return new ContextScopedSubscriber<GId, OperationRepresentation>(deviceControlApi.getNotificationsSubscriber(), contextService);
    }
}