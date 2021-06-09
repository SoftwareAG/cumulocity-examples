package c8y.example.notification.helloworld;

import c8y.example.notification.helloworld.websocket.NotificationCallback;
import c8y.example.notification.helloworld.websocket.NotificationConsumerWebSocket;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionFilterRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationSubscriptionRepresentation;
import com.cumulocity.rest.representation.reliable.notification.NotificationTokenRequestRepresentation;
import com.cumulocity.sdk.client.messaging.notifications.NotificationSubscriptionApi;
import com.cumulocity.sdk.client.messaging.notifications.NotificationSubscriptionFilter;
import com.cumulocity.sdk.client.messaging.notifications.TokenApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Test class
 */

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class NotificationService {

    private final TokenApi tokenApi;
    private final NotificationSubscriptionApi subscriptionApi;

    @Value("${subscription.source.id}")
    private String sourceId;

    @EventListener
    public void onSubscriptionAdded(MicroserviceSubscriptionAddedEvent event) throws InterruptedException, IOException, URISyntaxException {
        log.info("Subscription added for Tenant ID: <{}> ", event.getCredentials().getTenant());
        init();
    }

    public void init() throws URISyntaxException, IOException, InterruptedException {
        final String subscriber = "sub";
        final String subscription = "test" + sourceId + "subscription";
        final ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(sourceId));

        createSubscription(subscription, source);
        final String token = createToken(subscriber, subscription);

        final String webSocketUrl = "ws://localhost:8080/c8y/relnotif/consumer/?token=" + token;

        NotificationConsumerWebSocket socket = new NotificationConsumerWebSocket(new NotificationCallback() {
            @Override
            public void onNotification(List<String> headers, String notification) {
                for (String header : headers) {
                    log.info("header " + header);
                }
                log.info("notification " + notification);
            }

            @Override
            public void close() {
                log.info("close");
            }
        });

        socket.run(new URI(webSocketUrl), 60, 0);

    }

    private String createToken(String subscriber, String subscription) {
        final NotificationTokenRequestRepresentation tokenRequestRepresentation = new NotificationTokenRequestRepresentation(
                subscriber,
                subscription,
                1440,
                false);
        return tokenApi.create(tokenRequestRepresentation).getTokenString();
    }

    private void createSubscription(String subscription, ManagedObjectRepresentation source) {
        final NotificationSubscriptionFilterRepresentation filterRepresentation = new NotificationSubscriptionFilterRepresentation();
        filterRepresentation.setApis(List.of("measurements"));
        filterRepresentation.setTypeFilter("c8y_Speed");

        final NotificationSubscriptionRepresentation subscriptionRepresentation = new NotificationSubscriptionRepresentation();
        subscriptionRepresentation.setContext("mo");
        subscriptionRepresentation.setSubscription(subscription);
        subscriptionRepresentation.setSource(source);
        subscriptionRepresentation.setSubscriptionFilter(filterRepresentation);
        subscriptionRepresentation.setFragmentsToCopy(List.of("c8y_SpeedMeasurement", "c8y_MaxSpeedMeasurement"));

        subscriptionApi.subscribe(subscriptionRepresentation);
    }

}
