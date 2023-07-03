package c8y.example.notification.microservice;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
public class Properties {

    @Value("${example.source.id}")
    private String sourceId;

    @Value("${example.websocket.url}")
    private String webSocketBaseUrl;

    @Value("${example.subscriber}")
    private String subscriber;

    @Value("${example.websocket.library:@null}")
    private String webSocketLibrary;
}
