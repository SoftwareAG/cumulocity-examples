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

    public void create(NotificationSubscriptionRepresentation subscriptionRepresentation) {
        subscriptionApi.subscribe(subscriptionRepresentation);
    }

    public NotificationSubscriptionCollection getByFilter(NotificationSubscriptionFilter filter) {
        return subscriptionApi.getSubscriptionsByFilter(filter);
    }

    public boolean exists(GId source) {
        final NotificationSubscriptionFilter filter = new NotificationSubscriptionFilter().bySource(source);
        return !getByFilter(filter).get().getSubscriptions().isEmpty();
    }

}
