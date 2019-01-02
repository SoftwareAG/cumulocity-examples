package c8y.mibparser;

import c8y.mibparser.configuration.MicroserviceConfiguration;
import c8y.mibparser.configuration.Properties;
import com.cumulocity.microservice.subscription.model.MicroserviceMetadataRepresentation;
import com.cumulocity.microservice.subscription.repository.MicroserviceRepository;
import com.cumulocity.rest.representation.application.ApplicationRepresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.cumulocity.microservice.subscription.model.MicroserviceMetadataRepresentation.microserviceMetadataRepresentation;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MicroserviceRegistration {

    private final Properties properties;

    private final MicroserviceRepository microserviceRepository;

    private final MicroserviceConfiguration microserviceConfiguration;

    private ApplicationRepresentation application;

    @PostConstruct
    public void init() throws Exception {
        registerMicroservice();
    }

    private void registerMicroservice() {
        List<String> requiredRoles = microserviceConfiguration.microserviceRepresentation().getRequiredRoles();
        MicroserviceMetadataRepresentation.MicroserviceMetadataRepresentationBuilder builder = microserviceMetadataRepresentation();
        for (String requiredRole : requiredRoles) {
            builder = builder.requiredRole(requiredRole);
        }
        MicroserviceMetadataRepresentation metadataRepresentation = builder.build();
        this.application = microserviceRepository.register(properties.getApplicationName(), metadataRepresentation);
    }

    public ApplicationRepresentation getApplication() {
        return application;
    }
}
