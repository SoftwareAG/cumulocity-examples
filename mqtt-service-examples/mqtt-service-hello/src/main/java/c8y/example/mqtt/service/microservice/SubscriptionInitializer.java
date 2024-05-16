package c8y.example.mqtt.service.microservice;

import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionRemovedEvent;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.mqtt.service.sdk.MqttServiceApi;
import com.cumulocity.mqtt.service.sdk.listener.ConnectionListener;
import com.cumulocity.mqtt.service.sdk.subscriber.Subscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static com.cumulocity.mqtt.service.sdk.subscriber.SubscriberConfig.subscriberConfig;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionInitializer {

    private final MqttServiceApi mqttServiceApi;
    private final TopicProvider topicProvider;
    private final ExecutorService subscriptionExecutor;
    private final RetryTemplate subscriptionRetryTemplate;
    private final MicroserviceSubscriptionsService contextService;

    @EventListener
    public void subscriptionAdded(MicroserviceSubscriptionAddedEvent event) {
        // execute is async, so we don't block other event listeners
        subscriptionExecutor.submit(() -> {
            try {
                //try to subscribe with retry as the operation might fail in case of application update
                //when the old instance is still running
                subscriptionRetryTemplate.execute(context -> {
                    subscribe(event.getCredentials().getTenant());
                    return null;
                });
            } catch (Throwable e) {
                log.error("Failed to subscribe to tenant {}", event.getCredentials().getTenant(), e);
                throw new RuntimeException(e);
            }
        });
    }

    private void subscribe(final String tenantId) {
        contextService.runForTenant(tenantId, () -> {
            final String subscriberId = getSubscriberId(tenantId, topicProvider.getTopicName());
            final Optional<Subscriber> subscriberOptional = mqttServiceApi.getSubscriber(subscriberId);
            if (subscriberOptional.isEmpty()) {
                log.info("Subscribing to {}", subscriberId);
                final Subscriber subscriber = mqttServiceApi.buildSubscriber(subscriberConfig()
                        .id(subscriberId)
                        .subscriber("subscriber")
                        .topic(topicProvider.getTopicName())
                        .connectionListener(new LoggingConnectionListener())
                        .build());
                subscriber.subscribe(message -> log.info("Received message {}", message));
            } else if (!subscriberOptional.get().isConnected()) {
                log.info("Subscriber {} is not connected, resubscribing", subscriberId);
                subscriberOptional.get().resubscribe();
            }
            log.info("MQTT Service subscribed for tenant {} under {} topic", tenantId, topicProvider.getTopicName());
        });
    }

    @EventListener
    public void subscriptionRemoved(MicroserviceSubscriptionRemovedEvent event) {
        mqttServiceApi.closeSubscriber(getSubscriberId(event.getTenant(), topicProvider.getTopicName()));
    }

    private String getSubscriberId(final String tenant, final String topic) {
        return "subscriber:" + tenant + ":" + topic;
    }

    private static final class LoggingConnectionListener implements ConnectionListener {
        @Override
        public void onError(final Throwable error, final String sourceId) {
            log.error("Subscriber {} error", sourceId, error);
        }

        @Override
        public void onDisconnected(final String reason, final String sourceId) {
            log.info("Subscriber {} disconnected because of {}", sourceId, reason);
        }
    }

}