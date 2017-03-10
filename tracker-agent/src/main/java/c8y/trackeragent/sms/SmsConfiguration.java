package c8y.trackeragent.sms;

import com.cumulocity.sms.client.SmsMessagingApi;
import com.cumulocity.sms.client.SmsMessagingApiImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsConfiguration {
    @Bean
    public SmsMessagingApi smsMessagingApi(@Value("${SMS.baseURL:}") String baseUrl, OptionsAuthorizationSupplier optionsAuth) {
        return new SmsMessagingApiImpl(baseUrl, "/smsmessaging/v1", optionsAuth);
    }
}
