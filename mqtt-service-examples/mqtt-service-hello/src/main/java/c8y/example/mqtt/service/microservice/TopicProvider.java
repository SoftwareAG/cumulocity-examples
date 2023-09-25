package c8y.example.mqtt.service.microservice;

import com.cumulocity.model.option.OptionPK;
import com.cumulocity.rest.representation.tenant.OptionRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.option.TenantOptionApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopicProvider {

    private static final String DEFAULT_TOPIC = "measurement";

    private final TenantOptionApi tenantOptionApi;

    public String getTopicName() {
        try {
            final OptionRepresentation option = tenantOptionApi.getOption(new OptionPK("mqtt-service-hello", "topic-name"));
            return option.getValue();
        } catch (SDKException e) {
            log.warn("Topic resolve error, using '{}' as a fallback. Response code: {}", DEFAULT_TOPIC, e.getHttpStatus());
            return DEFAULT_TOPIC;
        }
    }

}
