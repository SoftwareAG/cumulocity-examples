package c8y.example.notification.helloworld;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
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
}
