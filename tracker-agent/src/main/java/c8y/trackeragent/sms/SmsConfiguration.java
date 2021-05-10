package c8y.trackeragent.sms;

import com.cumulocity.sms.client.SmsMessagingApi;
import com.cumulocity.sms.client.SmsMessagingApiImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

@Configuration
public class SmsConfiguration {
    @Bean
    @Lazy
    @Scope(proxyMode = TARGET_CLASS)
    public SmsMessagingApi smsMessagingApi(@Value("${SMS.baseURL:}") String baseUrl, OptionsAuthorizationSupplier optionsAuth) {
        return new SmsMessagingApiImpl(baseUrl, "/smsmessaging", optionsAuth);
    }
}
