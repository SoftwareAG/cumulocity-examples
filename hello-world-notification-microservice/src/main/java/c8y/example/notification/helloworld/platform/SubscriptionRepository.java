package c8y.example.notification.helloworld.platform;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionRepresentation;
import com.cumulocity.sdk.client.messaging.notifications.NotificationSubscriptionApi;
import com.cumulocity.sdk.client.messaging.notifications.NotificationSubscriptionCollection;
import com.cumulocity.sdk.client.messaging.notifications.NotificationSubscriptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SubscriptionRepository {

    private final NotificationSubscriptionApi subscriptionApi;

    public NotificationSubscriptionRepresentation create(NotificationSubscriptionRepresentation subscriptionRepresentation) {
        return subscriptionApi.subscribe(subscriptionRepresentation);
    }

    public void delete(NotificationSubscriptionRepresentation subscriptionRepresentation) {
        subscriptionApi.delete(subscriptionRepresentation);
    }

    public NotificationSubscriptionCollection getByFilter(NotificationSubscriptionFilter filter) {
        return subscriptionApi.getSubscriptionsByFilter(filter);
    }

}
