package c8y.example.mqtt.service.microservice;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import com.cumulocity.mqtt.service.sdk.MqttServiceApi;
import com.cumulocity.sdk.client.messaging.notifications.TokenApi;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.PropertyResolver;
import org.springframework.retry.support.RetryTemplate;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;

@MicroserviceApplication
public class MqttServiceMicroserviceConfiguration {

    @Bean
    public ExecutorService subscriptionExecutor() {
        return Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("sub-executor-%d").build());
    }

    @Bean
    public RetryTemplate subscriptionRetryTemplate() {
        return RetryTemplate.builder()
                .retryOn(Throwable.class)
                .fixedBackoff(SECONDS.toMillis(10))
                .maxAttempts(5)
                .build();
    }

    @Bean(destroyMethod = "close")
    public MqttServiceApi mqttServiceApi(final TokenApi tokenApi, final PropertyResolver propertyResolver) {
        final String url = "ws://" + URI.create(propertyResolver.getRequiredProperty("C8Y.baseURL")).getHost();
        return MqttServiceApi.webSocket()
                .url(url)
                .tokenApi(tokenApi)
                .build();
    }

}
