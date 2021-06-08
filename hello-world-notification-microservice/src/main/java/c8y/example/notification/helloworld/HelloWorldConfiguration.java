package c8y.example.notification.helloworld;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import com.cumulocity.microservice.subscription.model.MicroserviceMetadataRepresentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static com.cumulocity.microservice.subscription.model.MicroserviceMetadataRepresentation.microserviceMetadataRepresentation;

@MicroserviceApplication
public class HelloWorldConfiguration {

    @Bean
    @Primary
    public MicroserviceMetadataRepresentation helloWorldMicroserviceMetadata() {
        return microserviceMetadataRepresentation()
                .requiredRole("ROLE_RELIABLE_NOTIFICATION_ADMIN")
                .build();
    }
}
