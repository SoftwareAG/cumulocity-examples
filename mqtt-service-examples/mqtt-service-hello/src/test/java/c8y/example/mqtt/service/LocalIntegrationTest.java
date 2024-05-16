package c8y.example.mqtt.service;

import com.cumulocity.model.authentication.CumulocityCredentialsFactory;
import com.cumulocity.mqtt.service.sdk.MqttServiceApi;
import com.cumulocity.mqtt.service.sdk.model.MqttServiceMessage;
import com.cumulocity.mqtt.service.sdk.model.MqttServiceMetadata;
import com.cumulocity.mqtt.service.sdk.publisher.Publisher;
import com.cumulocity.mqtt.service.sdk.publisher.PublisherConfig;
import com.cumulocity.mqtt.service.sdk.subscriber.Subscriber;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.cumulocity.mqtt.service.sdk.subscriber.SubscriberConfig.subscriberConfig;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class LocalIntegrationTest {

    @Test
    @Disabled
    void shouldCreateSubscriber() {
        // given
        final List<MqttServiceMessage> receivedMessages = new ArrayList<>();
        final String url = "cumulocity.default.svc.cluster.local";
        final String tenantId = "tenant";
        final String username = "user";
        final String password = "pass";
        final String topic = "measurement";

        Platform platform = buildPlatform(url, tenantId, username, password);
        final MqttServiceApi mqttServiceApi = MqttServiceApi.webSocket()
                .url("ws://" + url)
                .tokenApi(platform.getTokenApi())
                .build();

        final Subscriber subscriber = buildSubscriber(mqttServiceApi, topic);
        subscriber.subscribe(receivedMessages::add);
        assertThat(subscriber.isConnected()).isTrue();

        // when
        sendMessage(mqttServiceApi, topic);

        //then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            assertThat(receivedMessages).hasSize(1);
        });

        // cleanup
        mqttServiceApi.close();
        platform.close();
    }

    private Subscriber buildSubscriber(final MqttServiceApi mqttServiceApi, final String topic) {
        return mqttServiceApi.buildSubscriber(subscriberConfig()
                .id("mySubscriber")
                .subscriber("subscriber")
                .topic(topic)
                .build());
    }

    private void sendMessage(final MqttServiceApi mqttServiceApi, final String topic) {
        try (final Publisher publisher = mqttServiceApi.buildPublisher(PublisherConfig.publisherConfig()
                .id("myPublisher")
                .topic(topic)
                .build())) {
            publisher.publish(new MqttServiceMessage("Hello, world!".getBytes(UTF_8), MqttServiceMetadata.builder().build()));
        }
    }

    private Platform buildPlatform(String url, String tenantId, String username, String password) {
        return new PlatformImpl(
                "http://" + url, new CumulocityCredentialsFactory()
                .withTenant(tenantId)
                .withUsername(username)
                .withPassword(password)
                .getCredentials()
        );
    }

}
