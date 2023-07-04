package c8y.example.notification.microservice;

import c8y.example.notification.common.platform.SubscriptionRepository;
import c8y.example.notification.common.platform.TokenService;
import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import com.cumulocity.microservice.subscription.model.MicroserviceMetadataRepresentation;
import com.cumulocity.sdk.client.messaging.notifications.NotificationSubscriptionApi;
import com.cumulocity.sdk.client.messaging.notifications.TokenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static com.cumulocity.microservice.subscription.model.MicroserviceMetadataRepresentation.microserviceMetadataRepresentation;

@MicroserviceApplication
public class HelloWorldConfiguration {

    @Bean
    @Primary
    public MicroserviceMetadataRepresentation helloWorldMicroserviceMetadata() {
        return microserviceMetadataRepresentation()
                .requiredRole("ROLE_NOTIFICATION_2_ADMIN")
                .build();
    }

    @Bean
    @Autowired
    public TokenService tokenService(TokenApi tokenApi) {
        return new TokenService(tokenApi);
    }

    @Bean
    @Autowired
    public SubscriptionRepository subscriptionRepository(NotificationSubscriptionApi notificationSubscriptionApi) {
        return new SubscriptionRepository(notificationSubscriptionApi);
    }
}
