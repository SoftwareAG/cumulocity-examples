package c8y.example.mqtt.service;

import com.cumulocity.model.authentication.CumulocityCredentialsFactory;
import com.cumulocity.mqtt.service.client.MqttClient;
import com.cumulocity.mqtt.service.client.MqttPublisher;
import com.cumulocity.mqtt.service.client.MqttSubscriber;
import com.cumulocity.mqtt.service.client.PublisherConfig;
import com.cumulocity.mqtt.service.client.model.MqttMessage;
import com.cumulocity.mqtt.service.client.model.MqttMetadata;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.cumulocity.mqtt.service.client.SubscriberConfig.subscriberConfig;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class LocalIntegrationTest {

    @Test
    @Disabled
    void shouldCreateSubscriber() {
        // given
        final List<MqttMessage> receivedMessages = new ArrayList<>();
        final String url = "cumulocity.default.svc.cluster.local";
        final String tenantId = "tenant";
        final String username = "user";
        final String password = "pass";
        final String topic = "measurement";

        Platform platform = buildPlatform(url, tenantId, username, password);
        final MqttClient client = MqttClient.webSocket()
                .url("ws://" + url)
                .tokenApi(platform.getTokenApi())
                .build();

        final MqttSubscriber subscriber = getBuildSubscriber(client, topic);
        subscriber.subscribe(receivedMessages::add);
        assertThat(subscriber.isConnected()).isTrue();

        // when
        sendMessage(client, topic);

        //then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            assertThat(receivedMessages).hasSize(1);
        });

        // cleanup
        client.close();
        platform.close();
    }

    private MqttSubscriber getBuildSubscriber(final MqttClient client, final String topic) {
        return client.buildSubscriber(subscriberConfig()
                .id("mySubscriber")
                .subscriber("subscriber")
                .topic(topic)
                .build());
    }

    private void sendMessage(final MqttClient client, final String topic) {
        try (final MqttPublisher publisher = client.buildPublisher(PublisherConfig.publisherConfig()
                .id("myPublisher")
                .topic(topic)
                .build())) {
            publisher.publish(new MqttMessage("Hello, world!".getBytes(UTF_8), MqttMetadata.builder().build()));
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
