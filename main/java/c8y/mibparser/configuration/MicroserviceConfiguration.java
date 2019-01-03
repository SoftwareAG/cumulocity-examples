package c8y.mibparser.configuration;

import com.cumulocity.microservice.subscription.model.MicroserviceMetadataRepresentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.cumulocity.microservice.subscription.model.MicroserviceMetadataRepresentation.microserviceMetadataRepresentation;

@Configuration
public class MicroserviceConfiguration {
    public static final String ROLE_DEVICE_CONTROL_ADMIN = "ROLE_DEVICE_CONTROL_ADMIN";

    @Bean
    public MicroserviceMetadataRepresentation microserviceRepresentation() {
        return microserviceMetadataRepresentation()
                .requiredRole("ROLE_TENANT_MANAGEMENT_READ")
                .requiredRole("ROLE_OPTION_MANAGEMENT_READ")
                .requiredRole("ROLE_USER_MANAGEMENT_READ")
                .role(ROLE_DEVICE_CONTROL_ADMIN)
                .build();
    }
}
