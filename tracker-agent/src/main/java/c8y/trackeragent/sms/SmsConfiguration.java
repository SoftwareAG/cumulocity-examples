package c8y.trackeragent.sms;

import c8y.trackeragent.configuration.TrackerConfiguration;
import com.cumulocity.sms.client.SmsMessagingApi;
import com.cumulocity.sms.client.SmsMessagingApiImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsConfiguration {
    @Bean
    public SmsMessagingApi smsMessagingApi(TrackerConfiguration configuration, OptionsAuthorizationSupplier optionsAuth) {
        return new SmsMessagingApiImpl(configuration.getPlatformHost(), "service/messaging/smsmessaging/v1", optionsAuth);
    }
}
