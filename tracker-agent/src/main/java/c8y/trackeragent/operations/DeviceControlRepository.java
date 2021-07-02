package c8y.trackeragent.operations;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.notification.Subscriber;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DeviceControlRepository {
    private final DeviceControlApi deviceControlApi;
    private final Subscriber<GId, OperationRepresentation> subscriber;

    @Autowired
    public DeviceControlRepository(DeviceControlApi deviceControlApi, @Qualifier("deviceControlNotificationsSubscriber") Subscriber<GId, OperationRepresentation> subscriber) {
        this.deviceControlApi = deviceControlApi;
        this.subscriber = subscriber;
    }

    public Subscription<GId> subscribe(GId deviceId, SubscriptionListener<GId, OperationRepresentation> listener) {
        return this.subscriber.subscribe(deviceId, listener);
    }

    public void save(OperationRepresentation operation) {
        if (operation.getId() == null) {
            this.deviceControlApi.create(operation);
        } else {
            this.deviceControlApi.update(operation);
        }
    }
}
