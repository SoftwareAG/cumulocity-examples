package c8y.trackeragent_it.config;

import c8y.trackeragent.Main;
import com.cumulocity.sms.client.SmsMessagingApi;
import org.mockito.Mockito;
import org.springframework.context.annotation.*;

@Configuration
@Import(Main.class)
@PropertySource(value = { "classpath:tracker-agent-server.properties" })
public class ServerConfiguration {

    @Bean
    public SmsMessagingApi outgoingMessagingClient() {
        return Mockito.mock(SmsMessagingApi.class);
    }
}
